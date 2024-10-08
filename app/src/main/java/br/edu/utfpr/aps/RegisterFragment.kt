package br.edu.utfpr.aps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import br.edu.utfpr.aps.entidades.Usuario
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.ByteArrayOutputStream

class RegisterFragment : Fragment() {
    private val REQUEST_CODE_PERMISSION = 2
    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_TAKE_PHOTO = 3
    private var imageUri: Uri? = null
    private var imageByteArray: ByteArray? = null
    lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Solicitar permissões se necessário
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        }

        btCadastrar.setOnClickListener {
            val nome = txtRegNome.text.toString()
            val email = txtRegEmail.text.toString()
            val senha = txtRegSenha.text.toString()
            val senha2 = txtRegSenha2.text.toString()

            if (senha == senha2) {
                registrar(nome, email, senha)
            } else {
                Toast.makeText(activity, R.string.register_confirm_password, Toast.LENGTH_SHORT).show()
            }
        }

        // Botão para selecionar imagem da galeria ou tirar foto
        btnPickImage.setOnClickListener {
            showImageOptions()
        }
    }

    private fun showImageOptions() {
        val options = arrayOf("Tirar Foto", "Escolher da Galeria")
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Escolha uma opção")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> takePhoto() // Tirar foto
                1 -> pickPhoto() // Escolher da galeria
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)
        }
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                        imageByteArray = bitmapToByteArray(imageBitmap)
                    }
                }
                REQUEST_CODE_TAKE_PHOTO -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        imageByteArray = bitmapToByteArray(it)
                    }
                }
            }
        }
    }

    private fun registrar(nome: String, email: String, senha: String) {
        val usuarioDao = DatabaseClient.getUsuarioDao(requireContext())
        val usuario = imageByteArray?.let { Usuario(nome, email, senha, 0, 0, null, false, it) }
        val response = usuario?.let { usuarioDao.inserir(it) }

        if (response != null) {
            Toast.makeText(activity, R.string.register_confirm, Toast.LENGTH_SHORT).show()
            prefs = PreferenceManager.getDefaultSharedPreferences(activity)
            prefs.edit().clear().apply()
            prefs.edit().putString("email", email).apply()
            prefs.edit().putString("senha", senha).apply()

            val nav = Navigation.findNavController(requireActivity(), R.id.fragmentContent)
            nav.navigate(R.id.registerToLogin)
        } else {
            Toast.makeText(activity, R.string.register_fail, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, você pode abrir o seletor de imagens se necessário
            } else {
                Toast.makeText(requireContext(), "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}
