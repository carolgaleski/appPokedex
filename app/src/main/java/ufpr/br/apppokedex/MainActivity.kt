package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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

    fun login() {
        val retrofitClient  = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint  = retrofitClient.create(Endpoint::class.java)

        val loginData = LoginRequest(
            user = etLogin.text.toString(),
            password = etSenha.text.toString()
        )

        val callback = endpoint.auth(loginData)

        callback.enqueue(object  : Callback<Usuario> {
            override fun onResponse(
                call: Call<Usuario>,
                response: Response<Usuario>
            ) {
                if (response.isSuccessful && response.body()  != null) {

                    val usuario = response.body()!!

                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                    intent.putExtra("nome", usuario.nome)
                    intent.putExtra("email", usuario.email)
                    intent.putExtra("id", usuario.id)
                    startActivity(intent)
                    finish()

                }  else {
                    Toast.makeText(
                        baseContext,
                        "Login ou senha incorretos",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<Usuario>,
                t: Throwable
            ) {
                Toast.makeText(baseContext, "Erro na conexão: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }
}