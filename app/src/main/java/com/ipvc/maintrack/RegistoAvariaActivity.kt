package com.ipvc.maintrack

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.IOException

class RegistoAvariaActivity : AppCompatActivity() {

    private lateinit var descricaoEditText: EditText
    private lateinit var localizacaoEditText: EditText
    private lateinit var urgenciaGroup: RadioGroup
    private lateinit var photo1: ImageView
    private lateinit var submitButton: Button

    private var selectedImageUri: Uri? = null
    private var imageBase64: String? = null

    private val firestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
                Log.d("RegistoAvaria", "Imagem selecionada: $selectedImageUri")
                photo1.setImageURI(selectedImageUri)
                convertImageToBase64(selectedImageUri)
            } else {
                Log.d("RegistoAvaria", "Seleção da imagem cancelada ou falhou")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registo_avaria)

        descricaoEditText = findViewById(R.id.descricao)
        localizacaoEditText = findViewById(R.id.localizacao)
        urgenciaGroup = findViewById(R.id.urgencia_group)
        photo1 = findViewById(R.id.photo1)
        submitButton = findViewById(R.id.submit_button)

        photo1.setOnClickListener {
            Log.d("RegistoAvaria", "Botão de imagem clicado")
            pedirPermissaoEGaleria()
        }

        submitButton.setOnClickListener {
            Log.d("RegistoAvaria", "Botão de submeter clicado")
            registarAvaria()
        }
    }

    private fun pedirPermissaoEGaleria() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d("RegistoAvaria", "Permissão já atribuida")
            abrirGaleria()
        } else {
            Log.d("RegistoAvaria", "A pedir permissão para galeria")
            ActivityCompat.requestPermissions(this, arrayOf(permission), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("RegistoAvaria", "Permissão atribuida")
            abrirGaleria()
        } else {
            Log.w("RegistoAvaria", "Permissão recusada")
            Toast.makeText(this, "Permissão recusada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirGaleria() {
        Log.d("RegistoAvaria", "A abrir galeria...")
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun convertImageToBase64(uri: Uri?) {
        if (uri == null) {
            Log.e("RegistoAvaria", "URI nulo ao converter imagem")
            return
        }

        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val byteArray = outputStream.toByteArray()
            imageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Log.d("RegistoAvaria", "Imagem convertida para Base64 com ${byteArray.size} bytes")
        } catch (e: IOException) {
            Log.e("RegistoAvaria", "Erro ao converter imagem: ${e.message}", e)
        }
    }

    private fun registarAvaria() {
        val descricao = descricaoEditText.text.toString().trim()
        val localizacao = localizacaoEditText.text.toString().trim()
        val urgenciaId = urgenciaGroup.checkedRadioButtonId
        val urgencia = findViewById<RadioButton>(urgenciaId)?.text?.toString() ?: ""

        Log.d("RegistoAvaria", "Campos lidos: descrição='$descricao', local='$localizacao', urgência='$urgencia'")

        if (descricao.isEmpty() || localizacao.isEmpty() || urgencia.isEmpty()) {
            Log.w("RegistoAvaria", "Campos obrigatórios em falta")
            Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (user == null) {
            Log.e("RegistoAvaria", "Utilizador não autenticado")
            Toast.makeText(this, "Utilizador não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val avaria = hashMapOf(
            "descricao" to descricao,
            "localizacao" to localizacao,
            "urgencia" to urgencia,
            "imagemBase64" to imageBase64,
            "userId" to user.uid,
            "timestamp" to System.currentTimeMillis(),
            "status" to "pendente"
        )

        Log.d("RegistoAvaria", "A enviar avaria para Firestore: ${avaria.keys}")

        firestore.collection("malfunctions")
            .add(avaria)
            .addOnSuccessListener {
                Log.i("RegistoAvaria", "Avaria registada com sucesso no Firestore")
                Toast.makeText(this, "Avaria registada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("RegistoAvaria", "Erro ao registar a avaria: ${e.message}", e)
                Toast.makeText(this, "Erro ao registar a avaria", Toast.LENGTH_SHORT).show()
            }
    }
}
