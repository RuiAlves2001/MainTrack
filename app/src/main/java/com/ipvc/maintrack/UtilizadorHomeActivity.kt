package com.ipvc.maintrack

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class UtilizadorHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_utilizador_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val menuButton = findViewById<ImageButton>(R.id.menu_button)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Abre o menu lateral ao clicar no botão
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Fecha o menu e trata dos cliques nos itens
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {

                R.id.nav_registo_avaria -> {
                    val intent = Intent(this, RegistoAvariaActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_resolvidas -> {
                    // TODO: Abrir Avarias Resolvidas
                }
                R.id.nav_processo -> {
                    // TODO: Abrir Avarias em Processo
                }
                R.id.nav_pendentes -> {
                    // TODO: Abrir Avarias Pendentes
                }
                R.id.nav_perfil -> {
                    // TODO: Abrir Perfil
                }
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            true
        }
    }
}