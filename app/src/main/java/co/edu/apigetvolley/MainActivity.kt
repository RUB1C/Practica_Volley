package co.edu.apigetvolley

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import co.edu.apigetvolley.model.Post
import org.json.JSONException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val URL_API = "https://jsonplaceholder.typicode.com/posts"
        private const val REQUEST_TAG = "GET_POSTS"
    }

    private lateinit var lvTodos: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEstado: TextView

    private lateinit var requestQueue: RequestQueue
    private val listaPosts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicializarVistas()
        requestQueue = Volley.newRequestQueue(applicationContext)
        consumirApi()
    }

    private fun inicializarVistas() {
        lvTodos = findViewById(R.id.lvTodos)
        progressBar = findViewById(R.id.progressBar)
        tvEstado = findViewById(R.id.tvEstado)
    }

    private fun mostrarCargando(cargando: Boolean) {
        progressBar.visibility = if (cargando) View.VISIBLE else View.GONE
        lvTodos.visibility = if (cargando) View.GONE else View.VISIBLE
    }

    private fun mostrarError(mensaje: String) {
        mostrarCargando(false)
        tvEstado.text = mensaje
        tvEstado.visibility = View.VISIBLE
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    private fun procesarError(error: VolleyError) {
        var mensaje = error.message
        if (mensaje.isNullOrBlank()) {
            mensaje = "Verifique la conexión a internet."
        }
        mostrarError("Error en la solicitud: $mensaje")
    }

    private fun consumirApi() {
        mostrarCargando(true)

        val request = JsonArrayRequest(
            Request.Method.GET,
            URL_API,
            null,
            { response ->
                listaPosts.clear()
                try {
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val userId = item.getInt("userId")
                        val id = item.getInt("id")
                        val title = item.getString("title")
                        val body = item.getString("body")

                        val post = Post(userId, id, title, body)
                        listaPosts.add(post)
                    }

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        listaPosts
                    )
                    lvTodos.adapter = adapter
                    mostrarCargando(false)
                    tvEstado.visibility = View.GONE

                    Toast.makeText(
                        this,
                        "Se recibieron ${listaPosts.size} registros",
                        Toast.LENGTH_LONG
                    ).show()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    mostrarError("No fue posible procesar la respuesta.")
                }
            },
            { error ->
                procesarError(error)
            }
        )

        request.tag = REQUEST_TAG
        requestQueue.add(request)
    }

    override fun onStop() {
        super.onStop()
        if (::requestQueue.isInitialized) {
            requestQueue.cancelAll(REQUEST_TAG)
        }
    }
}
