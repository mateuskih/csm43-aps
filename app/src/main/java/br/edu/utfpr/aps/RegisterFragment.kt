package br.edu.utfpr.aps

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_register.*
import java.io.ByteArrayOutputStream
import java.util.*

class RegisterFragment : Fragment() {
    private val REQUEST_CODE_PERMISSION = 2
    private val REQUEST_CODE_PICK_IMAGE = 1
    private val REQUEST_CODE_TAKE_PHOTO = 3
    private var imageUri: Uri? = null
    private var imageByteArray: ByteArray? = null
    lateinit var prefs: SharedPreferences
    lateinit var cidade: String

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val usuarioDao: UsuarioDao by lazy { DatabaseClient.getUsuarioDao(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (hasLocationPermission()) {
            getLastLocation()
        } else {
            requestLocationPermission()
        }

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
            val nome = txtRegNome.text.toString().trim()
            val email = txtRegEmail.text.toString().trim()
            val senha = txtRegSenha.text.toString()
            val senha2 = txtRegSenha2.text.toString()
            val usuario = usuarioDao.buscarUsuarioByEmail(email)

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || senha2.isEmpty()) {
                Toast.makeText(activity, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(activity, "A senha deve ter pelo menos 6 caracteres.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != senha2) {
                Toast.makeText(activity, "As senhas não conferem.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (usuario != null) {
                Toast.makeText(activity, "Usuário já cadastrado com esse email.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registrar(nome, email, senha)
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
        val usuario = imageByteArray?.let { Usuario(nome, email, senha, 0, 0, null, false, cidade, it) }
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
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
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

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                getCityStateFromLocation(latitude, longitude)

            }
        }
    }

    private fun getCityStateFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.firstOrNull()

        address?.let {
            cidade = it.locality ?: "-"
        }
    }





}
