package xyz.dolphcode.tasktitans;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import xyz.dolphcode.tasktitans.database.Client;
import xyz.dolphcode.tasktitans.database.User;
import xyz.dolphcode.tasktitans.database.guilds.Guild;
import xyz.dolphcode.tasktitans.database.guilds.GuildTask;
import xyz.dolphcode.tasktitans.resources.Abilities;
import xyz.dolphcode.tasktitans.util.Util;

// The StatsActivity class is linked to the stats screen which is used to show player stats, activate skills and modify player stats
public class StatsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    User user;
    TextView strength;
    TextView intelligence;
    TextView constitution;
    TextView dexterity;
    String selectedSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);


        Util.setupConnectionChangedHandler(StatsActivity.this);

        Button backBtn = findViewById(R.id.statsBackButton);
        strength = findViewById(R.id.characterStrengthLabel);
        intelligence = findViewById(R.id.characterIntelLabel);
        constitution = findViewById(R.id.characterConstLabel);
        dexterity = findViewById(R.id.characterDextLabel);
        TextView money = findViewById(R.id.statsMoneyText);
        TextView mana = findViewById(R.id.statsManaText);
        TextView stats = findViewById(R.id.statsProfileInfo);
        ImageView profile = findViewById(R.id.statsProfilePicture);

        user = Client.getUser(getIntent().getStringExtra("ID"));

        updateStats(); // Update stats text views with user stats

        profile.setImageResource(Util.getProfileImage(this, user));

        stats.setText("Name: " + user.getDisplayName() + "\nHP: " + user.getHP() + "/" + user.getMaxHP() + "\nLevel: " + User.convertToLevel(user.getXP()));
        money.setText("" + user.getMoney()); // Show the player's balance
        mana.setText("" + user.getMana() + "/" + user.getMaxMana()); // Show the player's mana

        Spinner skills = findViewById(R.id.skillsDropdown);

        // Code based on code in the Android Studio documentation
        // https://developer.android.com/guide/topics/ui/controls/spinner
        String[] skillsArray = user.getSkill().split("-");
        selectedSkill = skillsArray[0];

        // ArrayAdapters are used to put items in a spinner
        // ArrayAdapters can be notified when their data set is changed allowing you to change the contents of a spinner (this is seen in other parts of the code)
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, skillsArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Button skillUseBtn = findViewById(R.id.skillUseBtn);
        skillUseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedSkill.toLowerCase().equals("heal") && user.getMana() > 20) {
                   user.heal(20, 20);
                   return;
                } else if (!user.getGuildID().equals("none")) {
                    Guild guild = Client.getGuild(user.getGuildID());
                    if (guild.getGuildTaskType() == GuildTask.BOSS && guild.getGuildBossHP() > 0) {
                        if (user.getMana() - 20 >= 0) {
                            guild.damageBoss(user.attackDamage());
                            user.depleteMana(20);
                        }
                    }
                }
                updateStats();
            }
        });

        skills.setAdapter(adapter);
        skills.setOnItemSelectedListener(this);
        // Documentation based code ends here

        Button strengthMod = findViewById(R.id.strengthModBtn);
        Button intelMod = findViewById(R.id.intelModBtn);
        Button constMod = findViewById(R.id.constModBtn);
        Button dextMod = findViewById(R.id.dextModBtn);

        // Add listeners to each modification button
        strengthMod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.addModifiers(0, 0, 1, 0);
                updateStats();
            }
        });

        intelMod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.addModifiers(0, 1, 0, 0);
                updateStats();
            }
        });

        constMod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.addModifiers(1, 0, 0, 0);
                updateStats();
            }
        });

        dextMod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user.addModifiers(0, 0, 0, 1);
                updateStats();
            }
        });

        Util.addSwitchWithUser(backBtn, GameActivity.class, StatsActivity.this, user.getID());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, // When an item is selected from the abilities dropdown the ability description should be shown
                               int pos, long id) {
        String key = parent.getItemAtPosition(pos).toString();
        TextView desc = findViewById(R.id.skillDesc);
        selectedSkill = key;
        desc.setText(Abilities.ABILITIES.get(key));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Required override
    }

    // Updates all stats textviews, used when first opening screen and adding stat modifiers
    public void updateStats() {
        strength.setText("Strength: " + (user.getBaseStrength() + user.getStrengthMod()));
        intelligence.setText("Intelligence: " + (user.getBaseIntel() + user.getIntelMod()));
        constitution.setText("Constitution: " + (user.getBaseConst() + user.getConstMod()));
        dexterity.setText("Dexterity: " + (user.getBaseDext() + user.getDextMod()));
    }
}
