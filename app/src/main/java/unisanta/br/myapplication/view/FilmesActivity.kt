package unisanta.br.myapplication.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import unisanta.br.myapplication.R
import unisanta.br.myapplication.databinding.ActivityFilmesBinding
import unisanta.br.myapplication.viewmodel.FilmesViewModel

class FilmesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFilmesBinding
    private lateinit var viewModel: FilmesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getStringExtra(EXTRA_EMAIL).isNullOrBlank()) {
            finish()
            return
        }
        enableEdgeToEdge()
        binding = ActivityFilmesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[FilmesViewModel::class.java]
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val barras = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
        val adapter = FilmeAdapter { filme ->
            viewModel.editar(filme)
            binding.edtNome.setText(filme.nome)
            binding.edtNota.setText(filme.nota.toString())
            binding.edtFoto.setText(filme.fotoUrl)
            binding.formulario.smoothScrollTo(0, 0)
            binding.edtNome.requestFocus()
        }
        binding.listaFilmes.layoutManager = LinearLayoutManager(this)
        binding.listaFilmes.adapter = adapter
        viewModel.filmes.observe(this) { filmes ->
            adapter.submitList(filmes)
            binding.txtVazio.isVisible = filmes.isEmpty()
        }
        viewModel.editando.observe(this) { id ->
            binding.btnSalvar.setText(if (id == null) R.string.adicionar_filme else R.string.salvar_alteracoes)
            binding.btnCancelar.isVisible = id != null
        }
        binding.btnSalvar.setOnClickListener {
            val erro = viewModel.salvar(
                binding.edtNome.text.toString(),
                binding.edtNota.text.toString(),
                binding.edtFoto.text.toString()
            )
            if (erro == null) {
                limparFormulario()
                Toast.makeText(this, R.string.filme_salvo, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
            }
        }
        binding.btnCancelar.setOnClickListener {
            viewModel.cancelarEdicao()
            limparFormulario()
        }
    }

    private fun limparFormulario() {
        binding.edtNome.text.clear()
        binding.edtNota.text.clear()
        binding.edtFoto.text.clear()
        binding.edtNome.clearFocus()
    }

    companion object {
        const val EXTRA_EMAIL = "email"
    }
}
