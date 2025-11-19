package ufpr.br.apppokedex

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import kotlin.system.exitProcess

class DashboardActivity : AppCompatActivity() {

    private lateinit var totalPokemon: TextView
    private lateinit var top3tipos: TextView
    private lateinit var top3habilidades: TextView
    private lateinit var listaPokemon: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        totalPokemon = findViewById(R.id.totalPokemon)
        top3tipos = findViewById(R.id.top3tipos)
        top3habilidades = findViewById(R.id.top3habilidades)
        listaPokemon = findViewById(R.id.listaPokemon)


        val nome = intent.getStringExtra("nome")
        val email = intent.getStringExtra("email")
        val id = intent.getStringExtra("id")

        totalPokemon.text = "Bem vindo, $nome"

        val botaoSair = findViewById<Button>(R.id.botaoSair)
        botaoSair.setOnClickListener {
            finishAffinity()   // fecha todas as activities do app
            exitProcess(0)
        }
    }
}