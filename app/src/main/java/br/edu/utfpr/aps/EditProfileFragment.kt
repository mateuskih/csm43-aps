package br.edu.utfpr.aps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import br.edu.utfpr.aps.bd.DatabaseClient
import br.edu.utfpr.aps.bd.dao.UsuarioDao
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment() {

    private val REQUEST_CODE_PERMISSION = 2
    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_TAKE_PHOTO = 3

    lateinit var prefs: SharedPreferences
    private var imageByteArray: ByteArray? = null
    lateinit var nome: String
    lateinit var email: String
    lateinit var senha: String

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val usuarioDao: UsuarioDao by lazy { DatabaseClient.getUsuarioDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PreferenceManager.getDefaultSharedPreferences(activity)

        nome = prefs.getString("nome", "")!!
        email = prefs.getString("email", "")!!
        senha = prefs.getString("senha", "")!!

        val usuario = usuarioDao.login(email, senha);

        val bitmap = BitmapFactory.decodeByteArray(usuario.foto, 0, usuario.foto.size)
        imageProfile.setImageBitmap(bitmap)

        imageProfile.setOnClickListener {
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

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}