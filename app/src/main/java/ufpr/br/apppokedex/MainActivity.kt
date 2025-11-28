package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.api.LoginRequest
import ufpr.br.apppokedex.model.Usuario
import ufpr.br.apppokedex.util.NetworkUtils

class MainActivity : AppCompatActivity() {
    private lateinit var etLogin: EditText
    private lateinit var etSenha: EditText
    private lateinit var botaoLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        etLogin = findViewById(R.id.etLogin)
        etSenha = findViewById(R.id.etSenha)
        botaoLogin = findViewById(R.id.botaoLogin)

        botaoLogin.setOnClickListener { login() }
    }

    private fun mostrarAlerta(titulo: String, mensagem: String, onOk: (() -> Unit)? = null) {

        runOnUiThread {
            val builder = AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK") { _, _ ->
                    onOk?.invoke()
                }
            builder.show()
        }
    }

    private fun login() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        val loginData = LoginRequest(
            user = etLogin.text.toString().trim(),
            password = etSenha.text.toString().trim()
        )

        val callback = endpoint.auth(loginData)

        callback.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful && response.body() != null) {

                    val usuario = response.body()!!

                    val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("usuarioLogado", etLogin.text.toString())
                        putString("nomeUsuario", usuario.nome)
                        putString("emailUsuario", usuario.email)
                        apply()
                    }

                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {
                    mostrarAlerta(
                        titulo = "Erro",
                        mensagem = "Login ou senha incorretos"
                    )
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                mostrarAlerta(
                    titulo = "Erro de conexão",
                    mensagem = "Não foi possível conectar: ${t.message}"
                )
            }
        })
    }
}