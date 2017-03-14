package gauge.soberupp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Gets the menu item, and moves the page to the appropiate page
     * @param item : The item of the menu to by selected
     * @return : the success of the menu change
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(!item.isChecked()) {
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_inputData) {
                startActivity(new Intent(this, AddData.class));
            } else if (id == R.id.nav_soberDiary) {
                startActivity(new Intent(this, SoberDiary.class));
            } else if (id == R.id.nav_graphs) {
                startActivity(new Intent(this, Graph.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, Settings.class));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
