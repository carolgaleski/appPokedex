package ufpr.br.apppokedex

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.model.PokemonRequest
import ufpr.br.apppokedex.model.PokemonResponse
import ufpr.br.apppokedex.util.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CadastroPokemonActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etTipo: EditText
    private lateinit var etHab1: EditText
    private lateinit var etHab2: EditText
    private lateinit var etHab3: EditText
    private lateinit var botaoCadastrar: Button

    private lateinit var usuarioLogado: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_pokemon)

        etNome = findViewById(R.id.etNome)
        etTipo = findViewById(R.id.etTipo)
        etHab1 = findViewById(R.id.etHab1)
        etHab2 = findViewById(R.id.etHab2)
        etHab3 = findViewById(R.id.etHab3)
        botaoCadastrar = findViewById(R.id.botaoCadastrar)

        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado = sharedPref.getString("usuarioLogado", "") ?: ""

        if (usuarioLogado.isEmpty()) {
            mostrarAlerta("Erro", "Usuário não enconstrado. Faça login novamente")
            finish()
            return
        }

        botaoCadastrar.setOnClickListener {
            cadastrarPokemon()
        }
    }

    private fun cadastrarPokemon()  {

        val nome = etNome.text.toString().trim()
        val tipo = etTipo.text.toString().trim()

        val habilidades = listOf(
            etHab1.text.toString().trim(),
            etHab2.text.toString().trim(),
            etHab3.text.toString().trim()
        ).filter { it.isNotEmpty() }

        if (nome.isEmpty() || tipo.isEmpty()) {
            mostrarAlerta("Erro", "Nome e tipo são obrigatórios")
            return
        }

        if (habilidades.isEmpty()) {
            mostrarAlerta("Erro", "Informe ao menos uma habilidade")
            return
        }

        if (habilidades.size > 3) {
            mostrarAlerta("Erro", "Máximo de 3 habilidades")
            return
        }

        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

        val request = PokemonRequest(
            nome = nome,
            tipo = tipo,
            habilidades = habilidades,
            usuario = usuarioLogado
        )

        endpoint.cadastrarPokemon(request)
            .enqueue(object : Callback<PokemonResponse> {
                override fun onResponse(
                    call: Call<PokemonResponse?>,
                    response: Response<PokemonResponse?>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()

                        if (body?.sucesso == true) {
                            mostrarAlerta("Sucesso", body.mensagem ?: "Pokemon cadastrado") {
                                setResult(RESULT_OK)
                                finish()
                            }
                        } else {
                            mostrarAlerta("Erro", body?.erro ?: "Erro ao cadastrar Pokemon")
                        }
                    } else  {
                        val msg = when (response.code()) {
                            409 -> "Já existe um Pokemon com esse nome"
                            400 -> "Campos obrigatórios faltando"
                            else -> "Erro ${response.code()} ao cadastrar Pokemon"
                        }
                        mostrarAlerta("Erro", msg)
                    }
                }

                override fun onFailure(
                    call: Call<PokemonResponse?>,
                    t: Throwable
                ) {
                    mostrarAlerta("Falha", "Erro  de conexão ${t.message}")
                }
            })
    }

    private fun mostrarAlerta(titulo: String, mensagem: String, onOk: (() -> Unit)? = null)  {
        val builder = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setPositiveButton("ok") { _, _ ->
                onOk?.invoke()
            }
            builder.show()
    }
}