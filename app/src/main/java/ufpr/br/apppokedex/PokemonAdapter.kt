package ufpr.br.apppokedex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ufpr.br.apppokedex.model.Pokemon

class PokemonAdapter(
    private var lista: MutableList<Pokemon>,
    private val onEditClick: ((Pokemon) -> Unit)? = null,
    private val onDeleteClick: ((Pokemon) -> Unit)? = null
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvNome: TextView = itemView.findViewById(R.id.tvNome)
        private val tvTipo: TextView = itemView.findViewById(R.id.tvTipo)
        private val tvHabilidades: TextView = itemView.findViewById(R.id.tvHabilidades)
        private val btnExcluir: ImageButton? = itemView.findViewById(R.id.btnExcluir)
        private val btnEditar: ImageButton? = itemView.findViewById(R.id.btnEditar)
        private val tvUsuario: TextView = itemView.findViewById(R.id.tvUsuario)

        fun bind(pokemon: Pokemon) {
            tvNome.text = pokemon.nome
            tvTipo.text = "Tipo: ${pokemon.tipo}"
            tvHabilidades.text = "Habilidades: ${pokemon.habilidades.joinToString(", ")}"
            //exibir nome user
            tvUsuario.text = "Cadastrado por: ${pokemon.usuario}"
            btnEditar?.setOnClickListener {
                onEditClick?.invoke(pokemon)
            }

            btnExcluir?.setOnClickListener {
                onDeleteClick?.invoke(pokemon)
            }

            // oculta os botões na dash se não houver o callback
            btnEditar?.visibility = if (onEditClick != null) View.VISIBLE else View.GONE
            btnExcluir?.visibility = if (onDeleteClick != null) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size

    fun updateList(novaLista: List<Pokemon>) {
        lista.clear()
        lista.addAll(novaLista)
        notifyDataSetChanged()
    }

    fun removeItem(pokemon: Pokemon) {
        val index = lista.indexOf(pokemon)
        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
