package edu.mx.unipolidgo.myfirebaseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.model.Document
import edu.mx.unipolidgo.myfirebaseproject.databinding.ActivityViewMenuBinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.mx.unipolidgo.myfirebaseproject.ViewMenuActivity.Product

class ViewMenuActivity : AppCompatActivity() {

    // Clase interna para representar un producto
    data class Product(
        val description: String,
        val price: String,
        val size: String,
        val image: String,
        val name: String
    )

    private var binding: ActivityViewMenuBinding? = null
    private val products: MutableList<Product> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewMenuBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(this@ViewMenuActivity)
            adapter = ProductAdapter(products)
        }

        readData()
    }

    private fun readData() {
        // Obtener una referencia a la colección "platillos" en Firestore
        val collectionRef = FirebaseFirestore.getInstance().collection("platillos")

        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Obtener los datos de cada documento
                    val name = document.getString("Name")
                    val image = document.getString("Image")
                    val description = document.getString("Description")
                    val size = document.getString("Size")
                    val price = document.getString("Price")

                    // Verificar si los datos son nulos
                    if (description != null && price != null && size != null && image != null && name != null) {
                        val product = Product(description, price, size, image, name)
                        products.add(product)
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                binding?.recyclerView?.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error al obtener los documentos: ${exception.message}")
                Toast.makeText(this, "Error al obtener los documentos", Toast.LENGTH_LONG).show()
            }
    }

    class ProductAdapter(private val products: List<Product>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.textName)
            val priceTextView: TextView = itemView.findViewById(R.id.textPrice)
            val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
            val sizeTextView: TextView = itemView.findViewById(R.id.textSize)
            val imageImageView: ImageView = itemView.findViewById(R.id.imageProduct)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val product = products[position]
            holder.nameTextView.text = product.name
            holder.priceTextView.text = product.price
            holder.descriptionTextView.text = product.description
            holder.sizeTextView.text = product.size
            Picasso.get().load(product.image)
                .resize(400, 400)
                .centerCrop()
                .into(holder.imageImageView)
        }

        override fun getItemCount(): Int {
            return products.size
        }
    }

    companion object {
        private const val TAG = "ViewMenuActivity"
    }
}
