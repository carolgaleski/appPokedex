package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import kotlin.system.exitProcess
import androidx.appcompat.widget.Toolbar
import ufpr.br.apppokedex.api.Endpoint
import ufpr.br.apppokedex.util.NetworkUtils


class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserNome: TextView
    private lateinit var totalPokemon: TextView
    private lateinit var top3tipos: TextView
    private lateinit var top3habilidades: TextView
    private lateinit var listaPokemon: RecyclerView


    private var usuarioLogado: String = ""
    private var nomeUsuario: String = "Usuário"

    private val cadastroPokemonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // atualiza a lista e outros itens
            carregarPokemon()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvUserNome = findViewById(R.id.tvUserNome)
        totalPokemon = findViewById(R.id.totalPokemon)
        top3tipos = findViewById(R.id.top3tipos)
        top3habilidades = findViewById(R.id.top3habilidades)
        listaPokemon = findViewById(R.id.listaPokemon)

        listaPokemon.layoutManager = LinearLayoutManager(this)

        val sharedPref = getSharedPreferences("AppPokedexPrefs", MODE_PRIVATE)
        usuarioLogado =  sharedPref.getString("usuarioLogado", "").orEmpty()
        nomeUsuario = sharedPref.getString("nomeUsuario", "Usuário").orEmpty()

        tvUserNome.text = "Bem vindo, $nomeUsuario"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        /*val botaoSair = findViewById<Button>(R.id.botaoSair)
        botaoSair.setOnClickListener {
            finishAffinity()   // fecha todas as activities do app
            exitProcess(0)
        }*/
    }

    override fun onResume() {
        super.onResume()
        carregarPokemon()
    }

    private fun carregarPokemon() {
        if (usuarioLogado.isEmpty()) return

        val retrofit = NetworkUtils.getRetrofitInstance("https://render-api-eqmo.onrender.com/")
        val endpoint = retrofit.create(Endpoint::class.java)

/*        val call = endpoint.listarPokemons(usuarioLogado)
        call.enqueue(object : Callback<List<Pokemon>> {
            override fun onResponse(call: Call<List<Pokemon>>, response: Response<List<Pokemon>>) {
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!
                    totalPokemon.text = "Pokémons Cadastrados: ${lista.size}"


                    // listaPokemon.adapter = PokemonAdapter(lista)
                } else {
                    totalPokemon.text = "Pokémons Cadastrados: 0"
                }
            }

            override fun onFailure(call: Call<List<Pokemon>>, t: Throwable) {
                totalPokemon.text = "Pokémons Cadastrados: 0"
            }
        })
    }*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dashboard_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuNovo -> {
                val intent = Intent(this, CadastroPokemonActivity::class.java)
                cadastroPokemonLauncher.launch(intent)
            }

            R.id.menuListar -> {
                startActivity(Intent(this, ListarPokemonActivity::class.java))
            }

            R.id.menuPesquisarTipo -> {
                startActivity(Intent(this, PesquisarTipoActivity::class.java))
            }

            R.id.menuPesquisarHab -> {
                startActivity(Intent(this, PesquisarHabilidadeActivity::class.java))
            }

            R.id.menuSair -> {
                finishAffinity()
                exitProcess(0)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}