package com.example.drhappy.witcherpedia;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {

	private static DBHelper instance;

	private static final String DATABASE_NAME = "witcherDB.db";
	private static final int DATABASE_VERSION = 1;

	static synchronized DBHelper getInstance(Context context) {
		// Use the application context, which will ensure that you
		// don't accidentally leak an Activity's context.
		if (instance == null) {
			instance = new DBHelper(context.getApplicationContext());
		}

		return instance;
	}

	private DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Create Faction table
		db.execSQL("CREATE TABLE Faction " +
				"( factionn TEXT PRIMARY KEY, playability TEXT, drawablen TEXT)");
		//Insert Faction entries
		insertFactions(db);

		//Create Overview table
		db.execSQL("CREATE TABLE Overview " +
				"( factionn TEXT PRIMARY KEY, drawablen TEXT, overview TEXT)");
		//Insert Overview entries
		insertOverviews(db);

		//Create Unit table
		db.execSQL("CREATE TABLE Unit " +
				"( unitn TEXT, factionn TEXT, category TEXT, class TEXT, nationality TEXT, priority INTEGER, drawablen TEXT, PRIMARY KEY (unitn, factionn))");
		//Insert Unit entries
		insertUnits(db);

		//Create Territory table
		db.execSQL("CREATE TABLE Territory " +
				"( num INTEGER, territoryn TEXT PRIMARY KEY, factionn TEXT, category TEXT, region TEXT, income INTEGER, characteristics TEXT)");
		//Insert Territory entries
		insertTerritories(db);

		//Create Bestiary file table
		db.execSQL("CREATE TABLE Bestiary " +
				"( bestiaryn TEXT PRIMARY KEY, drawablen TEXT)");
		//Insert Bestiary entries
		insertBestiaries(db);

		//Create Monster table
		db.execSQL("CREATE TABLE Monster " +
				"( monstern TEXT PRIMARY KEY, bestiaryn TEXT, category TEXT, hitpoints INTEGER, strength TEXT, speed INTEGER, initiative INTEGER, range INTEGER, abilities TEXT, priority INTEGER, drawablen TEXT, FOREIGN KEY(bestiaryn) REFERENCES Bestiary(bestiaryn))");
		//Insert Monster entries
		insertMonsters(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Faction");

		db.execSQL("DROP TABLE IF EXISTS Overview");

		db.execSQL("DROP TABLE IF EXISTS Unit");
		db.execSQL("DROP TABLE IF EXISTS Melee");
		db.execSQL("DROP TABLE IF EXISTS Ranged");
		db.execSQL("DROP TABLE IF EXISTS Mage");
		db.execSQL("DROP TABLE IF EXISTS Support");
		db.execSQL("DROP TABLE IF EXISTS Navy");
		db.execSQL("DROP TABLE IF EXISTS Monster");

		db.execSQL("DROP TABLE IF EXISTS Territory");
		db.execSQL("DROP TABLE IF EXISTS Fortified");

		db.execSQL("DROP TABLE IF EXISTS Bestiary");
		db.execSQL("DROP TABLE IF EXISTS Monster");

		onCreate(db);
	}

	Cursor getFactions() {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT factionn, drawablen FROM Faction ORDER BY playability", null);
	}

	Cursor getOverview(String factionn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT drawablen, overview FROM Overview WHERE factionn = ?", new String[]{factionn});
	}

	Cursor getUnits(String factionn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT unitn, drawablen FROM Unit WHERE factionn = ? ORDER BY priority", new String[]{factionn});
	}

	Cursor getUnit(String unitn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		String category;
		try (Cursor res = db.rawQuery("SELECT category FROM Unit WHERE unitn = ?", new String[]{unitn})) {
			res.moveToFirst();
			category = res.getString(res.getColumnIndex("category"));
		}

		return db.rawQuery("SELECT * FROM Unit NATURAL JOIN " + category + " WHERE Unit.unitn = ?", new String[]{unitn});
	}

	Cursor getTerritories(String factionn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT num, territoryn, category FROM Territory WHERE INSTR(?, factionn) > 0 OR INSTR(factionn, ?) > 0 ORDER BY category, num", new String[]{factionn, factionn});
	}

	Cursor getTerritory(String territoryn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		String category;
		try (Cursor res = db.rawQuery("SELECT category FROM Territory WHERE territoryn = ?", new String[]{territoryn})) {
			res.moveToFirst();
			category = res.getString(res.getColumnIndex("category"));
		}

		if (category.equals("Fortified")) {
			return db.rawQuery("SELECT * FROM Territory NATURAL JOIN Fortified WHERE Territory.territoryn = ?", new String[]{territoryn});
		} else {
			return db.rawQuery("SELECT * FROM Territory WHERE Territory.territoryn = ?", new String[]{territoryn});
		}
	}

	Cursor getBestiaries() {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT bestiaryn, drawablen FROM Bestiary", null);
	}

	Cursor getBestiaryMonsters(String bestiaryn) {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT monstern, drawablen FROM Monster WHERE bestiaryn = ? ORDER BY priority", new String[]{bestiaryn});
	}

	Cursor getMonster(String monstern) {
		SQLiteDatabase db = instance.getReadableDatabase();

		return db.rawQuery("SELECT * FROM Monster WHERE Monster.monstern = ?", new String[]{monstern});
	}

	private void insertFactions(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Faction VALUES('Aedirn, Lyria & Rivia','Major','lyriarivia_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Kaedwen','Major','kaedwen_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Temeria','Major','temeria_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Redania','Major','redania_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Nilfgaardian Empire','Major','nilfgaard_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Skellige & Cintra','Major','cintra_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Kovir & Poviss','Major','kovirpoviss_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Hengfors League','Minor','hengfors_league_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Novigrad','Minor','novigrad_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Scoia’tael','Minor','dol_blathanna_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Brokilon','Minor','brokilon_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Mahakam','Minor','mahakam_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Brugge','Minor','brugge_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Sodden','Minor','sodden_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Cidaris','Minor','cidaris_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Kerack','Minor','kerack_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Verden','Minor','verden_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Angren','Minor','angren_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Toussaint','Minor','toussaint_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Nazair','Minor','nazair_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Mag Turga','Minor','mag_turga_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Ebbing','Minor','ebbing_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Metinna','Minor','metinna_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Geso','Minor','geso_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Maecht','Minor','maecht_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Gemmera','Minor','gemmeria_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Rowan','Minor','rowan_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Ymlac','Minor','ymlac_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Etolia','Minor','etolia_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Vicovaro','Minor','vicovaro_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Ofier','Minor','ofier_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Zerrikania','Minor','zerrikania_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Haakland','Minor','haakland_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Wild Hunt','Minor','wild_hunt_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Strays of Spalla','Minor','strays_of_spalla_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Salamandra','Minor','salamandra_coa') ");
		db.execSQL("INSERT INTO Faction VALUES('Neutral','Minor','neutral_coa') ");
	}

	private void insertUnits(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Unit VALUES('Aedirnian Cavalier', 'Aedirn, Lyria & Rivia', 'Melee', 'Bronze', 'Aedirnian', 1, 'aedirnian_cavalier') ");
		db.execSQL("INSERT INTO Unit VALUES('Lyrian Huszár', 'Aedirn, Lyria & Rivia', 'Melee', 'Bronze', 'Lyrian', 2, 'lyrian_huszar') ");
		db.execSQL("INSERT INTO Unit VALUES('Aedirnian Mauler', 'Aedirn, Lyria & Rivia', 'Melee', 'Bronze', 'Aedirnian', 3, 'aedirnian_mauler') ");
		db.execSQL("INSERT INTO Unit VALUES('Lyrian Conscript', 'Aedirn, Lyria & Rivia', 'Melee', 'Bronze', 'Lyrian', 3, 'rivian_consript') ");
		db.execSQL("INSERT INTO Unit VALUES('Rivian Billman', 'Aedirn, Lyria & Rivia', 'Melee', 'Bronze', 'Lyrian', 4, 'lyrian_billman') ");
		db.execSQL("INSERT INTO Unit VALUES('Aedirnian Longbowman', 'Aedirn, Lyria & Rivia', 'Ranged', 'Bronze', 'Aedirnian', 5, 'aedirnian_longbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Lyrian Arbalist', 'Aedirn, Lyria & Rivia', 'Ranged', 'Bronze', 'Lyrian', 5, 'lyrian_arbalest') ");
		db.execSQL("INSERT INTO Unit VALUES('Rivian Warcrier', 'Aedirn, Lyria & Rivia', 'Support', 'Silver', 'Lyrian', 6, 'rivian_warcrier') ");
		db.execSQL("INSERT INTO Unit VALUES('Aedirnian Special Forces', 'Aedirn, Lyria & Rivia', 'Melee', 'Silver', 'Aedirnian', 7, 'aedirnian_special_forces') ");
		db.execSQL("INSERT INTO Unit VALUES('Mage', 'Aedirn, Lyria & Rivia','Mage', 'Silver', 'Common', 8, 'mage') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Aedirn, Lyria & Rivia', 'Support', 'Silver', 'Northern Realms', 9, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Aedirn, Lyria & Rivia', 'Support', 'Gold', 'Common', 9, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Aedirn, Lyria & Rivia', 'Melee', 'Silver', 'Common', 10, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Onager', 'Aedirn, Lyria & Rivia', 'Ranged', 'Silver', 'Common', 11, 'onager') ");
		db.execSQL("INSERT INTO Unit VALUES('Ballista', 'Aedirn, Lyria & Rivia', 'Ranged', 'Silver', 'Common', 12, 'ballista') ");

		db.execSQL("INSERT INTO Unit VALUES('Dun Banner Knight', 'Kaedwen', 'Melee', 'Bronze', 'Kaedweni', 1, 'dun_banner_knight') ");
		db.execSQL("INSERT INTO Unit VALUES('Dun Banner Scout', 'Kaedwen', 'Melee', 'Bronze', 'Kaedweni', 2, 'dun_banner_light') ");
		db.execSQL("INSERT INTO Unit VALUES('Kaedweni Bowman', 'Kaedwen', 'Ranged', 'Bronze', 'Kaedweni', 5, 'kaedweni_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Kaedweni Spearman', 'Kaedwen', 'Melee', 'Bronze', 'Kaedweni', 4, 'kaedweni_spearman') ");
		db.execSQL("INSERT INTO Unit VALUES('Kaedweni Militia', 'Kaedwen', 'Melee', 'Bronze', 'Kaedweni', 3, 'kaedweni_footmen') ");
		db.execSQL("INSERT INTO Unit VALUES('Kaedweni Serzhant', 'Kaedwen', 'Melee', 'Silver', 'Kaedweni', 3, 'kaedweni_sergeant') ");
		db.execSQL("INSERT INTO Unit VALUES('Kaedweni Siege Master', 'Kaedwen', 'Support', 'Silver', 'Kaedweni', 6, 'kaedweni_siege_master') ");
		db.execSQL("INSERT INTO Unit VALUES('Mage', 'Kaedwen', 'Mage', 'Silver', 'Common', 7, 'mage') ");
		db.execSQL("INSERT INTO Unit VALUES('Ban Ard Tutor', 'Kaedwen', 'Mage', 'Silver', 'Kaedweni', 8, 'ban_ard_tutor') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Kaedwen', 'Support', 'Silver', 'Northern Realms', 9, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Kaedwen', 'Support', 'Gold', 'Common', 9, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Kaedwen', 'Melee', 'Silver', 'Common', 10, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Trebuchet', 'Kaedwen', 'Ranged', 'Silver', 'Common', 11, 'trebuchet') ");
		db.execSQL("INSERT INTO Unit VALUES('Scorpion', 'Kaedwen', 'Ranged', 'Silver', 'Common', 12, 'scorpion') ");
		db.execSQL("INSERT INTO Unit VALUES('Reinforced Trebuchet', 'Kaedwen', 'Ranged', 'Silver', 'Kaedweni', 11, 'reinforced_trebuchet') ");
		db.execSQL("INSERT INTO Unit VALUES('Light Scorpion', 'Kaedwen', 'Ranged', 'Silver', 'Kaedweni', 12, 'light_scorpion') ");

		db.execSQL("INSERT INTO Unit VALUES('Temerian Knight', 'Temeria', 'Melee', 'Bronze', 'Temerian', 1, 'temerian_knight') ");
		db.execSQL("INSERT INTO Unit VALUES('Temerian Light Cavalry', 'Temeria', 'Melee', 'Bronze', 'Temerian', 2, 'temerian_light_cavalry') ");
		db.execSQL("INSERT INTO Unit VALUES('Temerian Crossbowman', 'Temeria', 'Ranged', 'Bronze', 'Temerian', 5, 'temerian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Temerian Man-At-Arms', 'Temeria', 'Melee', 'Bronze', 'Temerian', 3, 'temerian_man_at_arms') ");
		db.execSQL("INSERT INTO Unit VALUES('Temerian Conscript', 'Temeria', 'Melee', 'Bronze', 'Temerian', 4, 'temerian_conscript') ");
		db.execSQL("INSERT INTO Unit VALUES('Poor Fucking Infantry', 'Temeria', 'Melee', 'Bronze', 'Temerian', 4, 'poor_fucking_infantry') ");
		db.execSQL("INSERT INTO Unit VALUES('Temerian Drummer', 'Temeria', 'Support', 'Silver', 'Temerian', 6, 'temerian_drummer') ");
		db.execSQL("INSERT INTO Unit VALUES('Mage', 'Temeria', 'Mage', 'Silver', 'Common', 7, 'mage') ");
		db.execSQL("INSERT INTO Unit VALUES('Aretuza Adept', 'Temeria', 'Mage', 'Silver', 'Temerian', 7, 'aretuza_adept') ");
		db.execSQL("INSERT INTO Unit VALUES('Blue Stripes', 'Temeria', 'Melee', 'Silver', 'Temerian', 8, 'blue_stripes') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Temeria', 'Support', 'Silver', 'Northern Realms', 9, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Temeria', 'Support', 'Gold', 'Common', 9, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Temeria', 'Melee', 'Silver', 'Common', 10, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Trebuchet', 'Temeria', 'Ranged', 'Silver', 'Common', 11, 'trebuchet') ");
		db.execSQL("INSERT INTO Unit VALUES('Ballista', 'Temeria', 'Ranged', 'Silver', 'Common', 12, 'ballista') ");

		db.execSQL("INSERT INTO Unit VALUES('Redanian Knight', 'Redania', 'Melee', 'Bronze', 'Redanian', 1, 'redanian_knight') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Scout Cavalry', 'Redania', 'Melee', 'Bronze', 'Redanian', 2, 'redanian_light_cavalry') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Elite', 'Redania', 'Melee', 'Bronze', 'Redanian', 3, 'redanian_elite') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Halberdier', 'Redania', 'Melee', 'Bronze', 'Redanian', 4, 'redanian_halberdier') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Crossbowman', 'Redania', 'Ranged', 'Bronze', 'Redanian', 5, 'redanian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Longbowman', 'Redania', 'Ranged', 'Bronze', 'Redanian', 5, 'redanian_longbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Witch Hunter', 'Redania', 'Melee', 'Bronze', 'Novigradian', 3, 'witch_hunter') ");
		db.execSQL("INSERT INTO Unit VALUES('Mage', 'Redania', 'Mage', 'Silver', 'Common', 6, 'mage') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Free Company', 'Redania', 'Melee', 'Bronze', 'Redanian', 7, 'redanian_free_company') ");
		db.execSQL("INSERT INTO Unit VALUES('Tretogor Elite Troll Unit', 'Redania', 'Melee', 'Bronze', 'Redanian', 8, 'tretogor_elite_troll_unit') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Redania', 'Support', 'Silver', 'Northern Realms', 9, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Redania', 'Support', 'Gold', 'Common', 9, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Redania', 'Melee', 'Silver', 'Common', 10, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Onager', 'Redania', 'Ranged', 'Silver', 'Common', 11, 'onager') ");
		db.execSQL("INSERT INTO Unit VALUES('Scorpion', 'Redania', 'Ranged', 'Silver', 'Common', 12, 'scorpion') ");

		db.execSQL("INSERT INTO Unit VALUES('Nausicaa Cavalry', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 1, 'nauzicaa_cavalry') ");
		db.execSQL("INSERT INTO Unit VALUES('Venendal Cavalry', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 2, 'venendal_division') ");
		db.execSQL("INSERT INTO Unit VALUES('Alba Division', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 4, 'alba_spearman') ");
		db.execSQL("INSERT INTO Unit VALUES('Impera Brigade', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 4, 'impera_brigade') ");
		db.execSQL("INSERT INTO Unit VALUES('Impera Enforcer', 'Nilfgaardian Empire', 'Ranged', 'Bronze', 'Nilfgaardian', 5, 'impera_enforcers') ");
		db.execSQL("INSERT INTO Unit VALUES('Black Infantry Archer', 'Nilfgaardian Empire', 'Ranged', 'Bronze', 'Nilfgaardian', 5, 'black_infantry_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Daerlan Foot Soldier', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 3, 'daerlan_foot_soldier') ");
		db.execSQL("INSERT INTO Unit VALUES('Magne Division', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 3, 'magne_division') ");
		db.execSQL("INSERT INTO Unit VALUES('Slave Infantry', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 3, 'slave_infantry') ");
		db.execSQL("INSERT INTO Unit VALUES('Imperial Golem', 'Nilfgaardian Empire', 'Melee', 'Bronze', 'Nilfgaardian', 6, 'imperial_golem') ");
		db.execSQL("INSERT INTO Unit VALUES('Nilfgaardian Alchemist', 'Nilfgaardian Empire', 'Support', 'Silver', 'Nilfgaardian', 7, 'nilfgaardian_alchemist') ");
		db.execSQL("INSERT INTO Unit VALUES('Vicovaro Medic', 'Nilfgaardian Empire', 'Support', 'Silver', 'Nilfgaardian', 9, 'vicovaro_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Vicovaro Novice', 'Nilfgaardian Empire', 'Mage', 'Silver', 'Nilfgaardian', 8, 'vicovaro_novice') ");
		db.execSQL("INSERT INTO Unit VALUES('Combat Engineer', 'Nilfgaardian Empire', 'Support', 'Silver', 'Nilfgaardian', 7, 'combat_engineer') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Nilfgaardian Empire', 'Support', 'Gold', 'Common', 9, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Nilfgaardian Empire', 'Melee', 'Silver', 'Common', 10, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Onager', 'Nilfgaardian Empire', 'Ranged', 'Silver', 'Common', 11, 'onager') ");
		db.execSQL("INSERT INTO Unit VALUES('Scorpion', 'Nilfgaardian Empire', 'Ranged', 'Silver', 'Common', 12, 'scorpion') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Fire Scorpion', 'Nilfgaardian Empire', 'Ranged', 'Silver', 'Zerrikanian', 13, 'zerrikanian_fire_skorpion') ");
		db.execSQL("INSERT INTO Unit VALUES('Rot Tosser', 'Nilfgaardian Empire', 'Ranged', 'Silver', 'Nilfgaardian', 13, 'rot_tosser') ");

		db.execSQL("INSERT INTO Unit VALUES('Cintrian Knight', 'Skellige & Cintra', 'Melee', 'Bronze', 'Cintrian', 1, 'cintrian_knight') ");
		db.execSQL("INSERT INTO Unit VALUES('Cintrian Longswordsman', 'Skellige & Cintra', 'Melee', 'Bronze', 'Cintrian', 2, 'cintrian_longswordsman') ");
		db.execSQL("INSERT INTO Unit VALUES('Cintrian Pikeman', 'Skellige & Cintra', 'Melee', 'Bronze', 'Cintrian', 3, 'cintrian_pikeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Cintrian Crossbowman', 'Skellige & Cintra', 'Ranged', 'Bronze', 'Cintrian', 4, 'cintrian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Skellige & Cintra', 'Support', 'Silver', 'Northern Realms', 14, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Skellige & Cintra', 'Support', 'Gold', 'Common', 14, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Skellige & Cintra', 'Melee', 'Silver', 'Common', 15, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Trebuchet', 'Skellige & Cintra', 'Ranged', 'Silver', 'Common', 16, 'trebuchet') ");
		db.execSQL("INSERT INTO Unit VALUES('An Craite Raider', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 5, 'clan_an_craite_raider') ");
		db.execSQL("INSERT INTO Unit VALUES('An Craite Greatswordsman', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 5, 'clan_an_craite_greatswordsman') ");
		db.execSQL("INSERT INTO Unit VALUES('An Craite Warcrier', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 5, 'clan_an_craite_warcrier') ");
		db.execSQL("INSERT INTO Unit VALUES('Brokvar Hunter', 'Skellige & Cintra', 'Ranged', 'Bronze', 'Skelligan', 6, 'clan_brokvar_hunter') ");
		db.execSQL("INSERT INTO Unit VALUES('Drummond Warmonger', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 7, 'clan_drummond_warmonger') ");
		db.execSQL("INSERT INTO Unit VALUES('Drummond Shieldmaiden', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 7, 'clan_drummond_shieldmaiden') ");
		db.execSQL("INSERT INTO Unit VALUES('Dimun Pirate', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 8, 'clan_dimun_pirate') ");
		db.execSQL("INSERT INTO Unit VALUES('Dimun Corsair', 'Skellige & Cintra', 'Melee', 'Silver', 'Skelligan', 8, 'clan_dimun_corsair') ");
		db.execSQL("INSERT INTO Unit VALUES('Heymaey Skjald', 'Skellige & Cintra', 'Support', 'Silver', 'Skelligan', 9, 'heymaey_skjald') ");
		db.execSQL("INSERT INTO Unit VALUES('Heymaey Battlemaiden', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan', 9, 'clan_heymaey_battlemaiden') ");
		db.execSQL("INSERT INTO Unit VALUES('Tordarroch Mastersmith', 'Skellige & Cintra', 'Support', 'Silver', 'Skelligan', 10, 'clan_tordarroch_mastersmith') ");
		db.execSQL("INSERT INTO Unit VALUES('Tordarroch Flaminica', 'Skellige & Cintra', 'Support', 'Silver', 'Skelligan', 10, 'clan_tordarroch_flaminica') ");
		db.execSQL("INSERT INTO Unit VALUES('Tuisreach Axeman', 'Skellige & Cintra', 'Melee', 'Silver', 'Skelligan', 11, 'clan_tuirseach_axeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Vaedermakar', 'Skellige & Cintra', 'Mage', 'Silver', 'Skelligan', 12, 'vaedermakar') ");
		db.execSQL("INSERT INTO Unit VALUES('Druid', 'Skellige & Cintra', 'Mage', 'Silver', 'Common', 12, 'druid') ");
		db.execSQL("INSERT INTO Unit VALUES('Vildkaarl', 'Skellige & Cintra', 'Melee', 'Bronze', 'Skelligan/Svalblod', 13, 'vildkaarl') ");
		db.execSQL("INSERT INTO Unit VALUES('Priestess of Freya', 'Skellige & Cintra', 'Support', 'Silver', 'Skelligan', 14, 'priestess_of_freya') ");
		db.execSQL("INSERT INTO Unit VALUES('Whale Harpoon', 'Skellige & Cintra', 'Ranged', 'Silver', 'Skelligan', 16, 'whale_harpoon') ");

		db.execSQL("INSERT INTO Unit VALUES('Koviri Paladino', 'Kovir & Poviss', 'Melee', 'Bronze', 'Koviri', 1, 'koviri_paladino') ");
		db.execSQL("INSERT INTO Unit VALUES('Koviri Mounted Arquebusier', 'Kovir & Poviss', 'Ranged', 'Bronze', 'Koviri', 2, 'koviri_mounted_arquebusier') ");
		db.execSQL("INSERT INTO Unit VALUES('Koviri Picchierre', 'Kovir & Poviss', 'Melee', 'Bronze', 'Koviri', 3, 'koviri_picchierre') ");
		db.execSQL("INSERT INTO Unit VALUES('Koviri Condottiero', 'Kovir & Poviss', 'Melee', 'Bronze', 'Koviri', 3, 'koviri_condottiero') ");
		db.execSQL("INSERT INTO Unit VALUES('Koviri Balestriere', 'Kovir & Poviss', 'Ranged', 'Bronze', 'Koviri', 4, 'koviri_balestriere') ");
		db.execSQL("INSERT INTO Unit VALUES('Adieu’s Free Company', 'Kovir & Poviss', 'Melee', 'Bronze', 'Koviri', 5, 'adieus_free_company') ");
		db.execSQL("INSERT INTO Unit VALUES('Koviri Magister', 'Kovir & Poviss', 'Mage', 'Silver', 'Koviri', 6, 'koviri_magister') ");
		db.execSQL("INSERT INTO Unit VALUES('Field Medic', 'Kovir & Poviss', 'Support', 'Silver', 'Northern Realms', 7, 'field_medic') ");
		db.execSQL("INSERT INTO Unit VALUES('Spy', 'Kovir & Poviss', 'Support', 'Gold', 'Common', 7, 'spy') ");
		db.execSQL("INSERT INTO Unit VALUES('Battering Ram', 'Kovir & Poviss', 'Melee', 'Silver', 'Common', 8, 'battering_ram') ");
		db.execSQL("INSERT INTO Unit VALUES('Scorpion', 'Kovir & Poviss', 'Ranged', 'Silver', 'Common', 9, 'scorpion') ");
		db.execSQL("INSERT INTO Unit VALUES('Carambola', 'Kovir & Poviss', 'Ranged', 'Silver', 'Koviri', 10, 'carambola') ");

		db.execSQL("INSERT INTO Unit VALUES('Hengforsian Cataphract', 'Hengfors League', 'Melee', 'Bronze', 'Hengforsian', 1, 'hengforsian_cataphract') ");
		db.execSQL("INSERT INTO Unit VALUES('Hengforsian Halberdier', 'Hengfors League', 'Melee', 'Bronze', 'Hengforsian', 2, 'hengforsian_halberdier') ");
		db.execSQL("INSERT INTO Unit VALUES('Caingornian Swordsman', 'Hengfors League', 'Melee', 'Bronze', 'Hengforsian', 3, 'caingornian_swordsman') ");
		db.execSQL("INSERT INTO Unit VALUES('Malleorean Longbowman', 'Hengfors League', 'Ranged', 'Bronze', 'Hengforsian', 4, 'malleorean_longbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Royal Mercenary Guard', 'Hengfors League', 'Melee', 'Bronze', 'Hengforsian', 5, 'royal_mercenary_guard') ");

		db.execSQL("INSERT INTO Unit VALUES('Temple Guard', 'Novigrad', 'Melee', 'Bronze', 'Novigradian', 1, 'temple_guard') ");
		db.execSQL("INSERT INTO Unit VALUES('Order of the Flaming Rose Zealot', 'Novigrad', 'Melee', 'Bronze', 'Novigradian', 2, 'order_of_the_flaming_rose_zealot') ");
		db.execSQL("INSERT INTO Unit VALUES('Witch Hunter', 'Novigrad', 'Melee', 'Bronze', 'Novigradian', 3, 'witch_hunter') ");
		db.execSQL("INSERT INTO Unit VALUES('Redanian Crossbowman', 'Novigrad', 'Ranged', 'Bronze', 'Redanian', 4, 'redanian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Priest of the Eternal Fire', 'Novigrad', 'Support', 'Silver', 'Novigradian', 5, 'priest_of_the_eternal_fire') ");

		db.execSQL("INSERT INTO Unit VALUES('Vrihedd Vanguard', 'Scoia’tael', 'Melee', 'Bronze', 'Scoia’tael', 1, 'vrihedd_vanguard') ");
		db.execSQL("INSERT INTO Unit VALUES('Vrihedd Dragoon', 'Scoia’tael', 'Melee', 'Bronze', 'Scoia’tael', 2, 'vrihedd_dragoon') ");
		db.execSQL("INSERT INTO Unit VALUES('Vrihedd Brigade', 'Scoia’tael', 'Ranged', 'Bronze', 'Scoia’tael', 3, 'vrihedd_brigade') ");
		db.execSQL("INSERT INTO Unit VALUES('Dol Blathanna Defender', 'Scoia’tael', 'Melee', 'Bronze', 'Scoia’tael', 4, 'dol_blathanna_defender') ");
		db.execSQL("INSERT INTO Unit VALUES('Dol Blathanna Archer', 'Scoia’tael', 'Ranged', 'Bronze', 'Scoia’tael', 5, 'dol_blathanna_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Elven Wardancer', 'Scoia’tael', 'Melee', 'Bronze', 'Scoia’tael', 6, 'elven_wardancer') ");
		db.execSQL("INSERT INTO Unit VALUES('Elven Marksman', 'Scoia’tael', 'Ranged', 'Bronze', 'Scoia’tael', 7, 'elven_marksman') ");
		db.execSQL("INSERT INTO Unit VALUES('Elven Deadeye', 'Scoia’tael', 'Melee', 'Silver', 'Scoia’tael', 8, 'elven_deadeye') ");
		db.execSQL("INSERT INTO Unit VALUES('Vrihedd Sapper', 'Scoia’tael', 'Support', 'Silver', 'Scoia’tael', 9, 'vrihedd_sapper') ");

		db.execSQL("INSERT INTO Unit VALUES('Dryad Ranger', 'Brokilon', 'Ranged', 'Bronze', 'Brokilon', 1, 'dryad_ranger') ");
		db.execSQL("INSERT INTO Unit VALUES('Dryad Grovekeeper', 'Brokilon', 'Mage', 'Bronze', 'Brokilon', 2, 'dryad_grovekeeper') ");

		db.execSQL("INSERT INTO Unit VALUES('Mahakam Defender', 'Mahakam', 'Melee', 'Bronze', 'Mahakam', 1, 'mahakam_defender') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Axeman', 'Mahakam', 'Melee', 'Bronze', 'Mahakam', 2, 'mahakam_axeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Marauder', 'Mahakam', 'Melee', 'Bronze', 'Mahakam', 3, 'mahakam_marauder') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Skirmisher', 'Mahakam', 'Ranged', 'Bronze', 'Mahakam', 4, 'mahakam_skirmisher') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Arbalist', 'Mahakam', 'Ranged', 'Bronze', 'Mahakam', 5, 'mahakam_arbalist') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Shield-Bearer', 'Mahakam', 'Support', 'Bronze', 'Mahakam', 6, 'mahakam_shield_bearer') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Blacksmith', 'Mahakam', 'Support', 'Silver', 'Mahakam', 7, 'mahakam_blacksmith') ");
		db.execSQL("INSERT INTO Unit VALUES('Mahakam Pyrotechnician', 'Mahakam', 'Support', 'Silver', 'Mahakam', 8, 'mahakam_pyrotechnician') ");

		db.execSQL("INSERT INTO Unit VALUES('Bruggian Lancer', 'Brugge', 'Melee', 'Bronze', 'Bruggian', 1, 'bruggian_lancer') ");
		db.execSQL("INSERT INTO Unit VALUES('Bruggian Landsknecht', 'Brugge', 'Melee', 'Bronze', 'Bruggian', 2, 'bruggian_axeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Bruggian Crossbowman', 'Brugge', 'Ranged', 'Bronze', 'Bruggian', 3, 'bruggian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Bruggian Slinger', 'Brugge', 'Ranged', 'Bronze', 'Bruggian', 4, 'bruggian_slinger') ");

		db.execSQL("INSERT INTO Unit VALUES('Soddener Man-at-Arms', 'Sodden', 'Melee', 'Bronze', 'Soddener', 1, 'soddener_man_at_arms') ");
		db.execSQL("INSERT INTO Unit VALUES('Soddener Axeman', 'Sodden', 'Melee', 'Bronze', 'Soddener', 2, 'soddener_axeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Soddener Archer', 'Sodden', 'Ranged', 'Bronze', 'Soddener', 3, 'soddener_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Free Slopes Lancer', 'Sodden', 'Melee', 'Bronze', 'Soddener', 4, 'free_slopes_lancer') ");
		db.execSQL("INSERT INTO Unit VALUES('Free Slopes Crusher', 'Sodden', 'Melee', 'Bronze', 'Soddener', 5, 'free_slopes_crusher') ");
		db.execSQL("INSERT INTO Unit VALUES('Free Slopes Skirmisher', 'Sodden', 'Ranged', 'Bronze', 'Soddener', 6, 'free_slopes_skirmisher') ");

		db.execSQL("INSERT INTO Unit VALUES('Cidarian Companion', 'Cidaris', 'Melee', 'Bronze', 'Cidarian', 1, 'cidaris_companion') ");
		db.execSQL("INSERT INTO Unit VALUES('Cidarian Hoplite', 'Cidaris', 'Melee', 'Bronze', 'Cidarian', 2, 'cidaris_hoplite') ");
		db.execSQL("INSERT INTO Unit VALUES('Cidarian Toxotai', 'Cidaris', 'Ranged', 'Bronze', 'Cidarian', 3, 'cidaris_toxotai') ");
		db.execSQL("INSERT INTO Unit VALUES('Cidarian Peltast', 'Cidaris', 'Melee', 'Bronze', 'Cidarian', 4, 'cidaris_peltast') ");

		db.execSQL("INSERT INTO Unit VALUES('Kerackian Huskarl', 'Kerack', 'Melee', 'Bronze', 'Kerackian', 1, 'kerackian_huskarl') ");
		db.execSQL("INSERT INTO Unit VALUES('Kerackian Raider', 'Kerack', 'Melee', 'Bronze', 'Kerackian', 2, 'kerackian_raider') ");
		db.execSQL("INSERT INTO Unit VALUES('Kerackian Spearman', 'Kerack', 'Melee', 'Bronze', 'Kerackian', 3, 'kerackian_spearman') ");
		db.execSQL("INSERT INTO Unit VALUES('Kerackian Archer', 'Kerack', 'Ranged', 'Bronze', 'Kerackian', 4, 'kerackian_archer') ");

		db.execSQL("INSERT INTO Unit VALUES('Verdenian Longbowman', 'Verden', 'Ranged', 'Bronze', 'Verdenian', 1, 'verdenian_longbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Conscript', 'Verden', 'Melee', 'Bronze', 'Verdenian', 2, 'verdenian_conscript') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Pikeman', 'Verden', 'Melee', 'Bronze', 'Verdenian', 3, 'verdenian_pikeman') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Royal Dragoon', 'Verden', 'Melee', 'Bronze', 'Verdenian', 4, 'verdenian_royal_dragoon') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Royal Guard', 'Verden', 'Melee', 'Bronze', 'Verdenian', 5, 'verdenian_royal_guard') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Guerilla Ambusher', 'Verden', 'Melee', 'Bronze', 'Verdenian', 6, 'verdenian_guerilla_ambusher') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Guerilla Bomber', 'Verden', 'Melee', 'Bronze', 'Verdenian', 7, 'verdenian_guerilla_bomber') ");
		db.execSQL("INSERT INTO Unit VALUES('Verdenian Guerilla Trapper', 'Verden', 'Melee', 'Bronze', 'Verdenian', 8, 'verdenian_guerilla_trapper') ");

		db.execSQL("INSERT INTO Unit VALUES('Angrenian Horseman', 'Angren', 'Melee', 'Bronze', 'Angrenian', 1, 'angrenian_horseman') ");
		db.execSQL("INSERT INTO Unit VALUES('Angrenian Protector', 'Angren', 'Melee', 'Bronze', 'Angrenian', 2, 'angrenian_protector') ");
		db.execSQL("INSERT INTO Unit VALUES('Angrenian Claimhdean', 'Angren', 'Melee', 'Bronze', 'Angrenian', 3, 'angrenian_claimhdean') ");
		db.execSQL("INSERT INTO Unit VALUES('Angrenian Bowman', 'Angren', 'Ranged', 'Bronze', 'Angrenian', 4, 'angrenian_bowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Druid of the Black Grove', 'Angren', 'Mage', 'Silver', 'Angrenian', 5, 'druid_of_the_black_grove') ");

		db.execSQL("INSERT INTO Unit VALUES('Beauclairoise Knight-Errant', 'Toussaint', 'Melee', 'Bronze', 'Toussaintoise', 1, 'beauclairoise_knight_errant') ");
		db.execSQL("INSERT INTO Unit VALUES('Beauclairoise Ducal Guard', 'Toussaint', 'Melee', 'Bronze', 'Toussaintoise', 2, 'beauclairoise_ducal_guard') ");
		db.execSQL("INSERT INTO Unit VALUES('Toussaintoise Crossbowman', 'Toussaint', 'Ranged', 'Bronze', 'Toussaintoise', 3, 'toussaintoise_crossbowman') ");

		// missing a lot

		db.execSQL("INSERT INTO Unit VALUES('Immortal', 'Ofier', 'Melee', 'Bronze', 'Ofieri', 1, 'ic_default_unit') ");
		db.execSQL("INSERT INTO Unit VALUES('Immortal Camelier', 'Ofier', 'Melee', 'Bronze', 'Ofieri', 2, 'ic_default_unit') ");
		db.execSQL("INSERT INTO Unit VALUES('Ofieri Charioteer', 'Ofier', 'Melee', 'Bronze', 'Ofieri', 3, 'ic_default_unit') ");
		db.execSQL("INSERT INTO Unit VALUES('Ofieri Runewright', 'Ofier', 'Support', 'Silver', 'Ofieri', 4, 'ic_default_unit') ");
		db.execSQL("INSERT INTO Unit VALUES('Ofieri Sorcerer', 'Ofier', 'Mage', 'Silver', 'Ofieri', 5, 'ic_default_unit') ");

		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Swordsman', 'Zerrikania', 'Melee', 'Bronze', 'Zerrikanian', 1, 'zerrikanian_swordsman') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Archer', 'Zerrikania', 'Ranged', 'Bronze', 'Zerrikanian', 2, 'zerrikanian_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Crossbowman', 'Zerrikania', 'Ranged', 'Bronze', 'Zerrikanian', 2, 'zerrikanian_crossbowman') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Cavalry', 'Zerrikania', 'Melee', 'Bronze', 'Zerrikanian', 3, 'zerrikanian_cavalry') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Camelier', 'Zerrikania', 'Melee', 'Bronze', 'Zerrikanian', 4, 'zerrikanian_camelier') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Mamluk', 'Zerrikania', 'Melee', 'Bronze', 'Zerrikanian', 5, 'zerrikanian_mamluk') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Camel Archer', 'Zerrikania', 'Ranged', 'Bronze', 'Zerrikanian', 6, 'zerrikanian_camel_archer') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Fire Warlock', 'Zerrikania', 'Mage', 'Silver', 'Zerrikanian', 7, 'zerrikanian_fire_warlock') ");
		db.execSQL("INSERT INTO Unit VALUES('Zerrikanian Fire Scorpion', 'Zerrikania', 'Ranged', 'Silver', 'Zerrikanian', 8, 'zerrikanian_fire_skorpion') ");
		db.execSQL("INSERT INTO Unit VALUES('War Elephant', 'Zerrikania', 'Ranged', 'Bronze', 'Zerrikanian', 9, 'war_elephant') ");

		// missing Haakland

		db.execSQL("INSERT INTO Unit VALUES('Red Rider', 'Wild Hunt', 'Melee', 'Bronze', 'Wild Hunt', 1, 'red_rider') ");
		db.execSQL("INSERT INTO Unit VALUES('Wild Hunt Warrior', 'Wild Hunt', 'Melee', 'Bronze', 'Wild Hunt', 2, 'wild_hunt_warrior') ");
		db.execSQL("INSERT INTO Unit VALUES('Wild Hunt Hound', 'Wild Hunt', 'Melee', 'Bronze', 'Wild Hunt', 3, 'wild_hunt_hound') ");
		db.execSQL("INSERT INTO Unit VALUES('Wild Hunt Navigator', 'Wild Hunt', 'Mage', 'Silver', 'Wild Hunt', 4, 'wild_hunt_navigator') ");

		db.execSQL("INSERT INTO Unit VALUES('Stray Cavalry', 'Strays of Spalla', 'Melee', 'Bronze', 'Strays', 1, 'stray_cavalry') ");
		db.execSQL("INSERT INTO Unit VALUES('Stray Marauder', 'Strays of Spalla', 'Melee', 'Bronze', 'Strays', 2, 'stray_marauder') ");
		db.execSQL("INSERT INTO Unit VALUES('Stray Slinger', 'Strays of Spalla', 'Ranged', 'Bronze', 'Strays', 3, 'stray_slinger') ");

		db.execSQL("INSERT INTO Unit VALUES('Salamandra Henchman', 'Salamandra', 'Melee', 'Bronze', 'Salamandra', 1, 'salamandra_henchman') ");
		db.execSQL("INSERT INTO Unit VALUES('Salamandra Mutant', 'Salamandra', 'Melee', 'Bronze', 'Salamandra', 2, 'salamandra_mutant') ");
		db.execSQL("INSERT INTO Unit VALUES('Salamandra Rabid Hound', 'Salamandra', 'Melee', 'Bronze', 'Salamandra', 3, 'salamandra_rabid_hound') ");


		//Create Melee table
		db.execSQL("CREATE TABLE Melee " +
				"( unitn TEXT PRIMARY KEY, specialization TEXT, strength TEXT, speed INTEGER, initiative INTEGER, cost INTEGER, armylimit TEXT, abilities TEXT, FOREIGN KEY(unitn) REFERENCES Unit(unitn))");

		// region Insert Melee entries
		db.execSQL("INSERT INTO Melee VALUES('Aedirnian Cavalier', 'Heavy Cavalry', '2.5 (2, plus 1 for every 2 units)', 3, 0, 10, '4 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Lyrian Huszár', 'Light Cavalry', '1', 4, 0, 6, '6 armies', '<br>&#8226; +3 Initiative against Ranged units<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Aedirnian Mauler', 'Heavily Armoured Infantry', '2', 2, 0, 8, '4 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"Ignores the armour of any Heavy units<br>&#8226; " +
				"+1 Strength bonus against Armoured monsters-like Ogroids, Conscructs, etc')");
		db.execSQL("INSERT INTO Melee VALUES('Lyrian Conscript', 'Light Infantry', '1.2 (1, plus 1 for every 5 units)', 2, 0, 4, '6 armies', '<br>&#8226; Up to 50 Conscripts have no need for Maintenance, and thus are excluded from any calculation of Unit Maintenance.')");
		db.execSQL("INSERT INTO Melee VALUES('Rivian Billman', 'Medium Polearm Infantry', '1', 2, 0, 4, '4 armies', '<br>&#8226; +1 Strength against cavalry')");
		db.execSQL("INSERT INTO Melee VALUES('Aedirnian Special Forces', 'Special Forces', '5', 3, 2, 16, '2 armies', '<br>&#8226; May perform Ambush<br>&#8226; " +
				"Can only be recruited in @Territory Description@Vengerberg@<br>&#8226; " +
				"Cannot be Focus-Fired or Ambushed<br>&#8226; " +
				"+2 Strength bonus when fighting inside the default Aedirnian, Lyrian and Rivian borders<br>&#8226; " +
				"+3 Strength bonus against Scoia’tael (both boni stack)')");
		db.execSQL("INSERT INTO Melee VALUES('Battering Ram', 'Melee Siege', '0', 1, 5, 30, '1 or 2 armies, for a total of 5 siege armies', '<br>&#8226; Can destroy gates to allow allied units to walk into fortifications and cities. Needs 3 Combat Rounds to reach the gates. Deals 2 Siege damage to gates each Round.<br>&#8226; " +
				"Can provide cover to 12 Allied units<br>&#8226; " +
				"Has 10 Hit Points, and 100 Armour against Ranged attacks')");
		db.execSQL("INSERT INTO Melee VALUES('Dun Banner Knight', 'Heavy Cavalry', '3', 3, 0, 15, '4 armies', '<br>&#8226; Beavers’ Infamy<br>&#8226; " +
				"2 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength Bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Dun Banner Scout', 'Light Cavalry', '1', 4, 0, 6, '6 armies', '<br>&#8226; May perform Charge with +1 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+3 Initiative against Ranged units') ");
		db.execSQL("INSERT INTO Melee VALUES('Kaedweni Spearman', 'Light Polearm Infantry', '1', 2, 0, 4, '6 armies', '<br>&#8226; +1 Strength against cavalry')");
		db.execSQL("INSERT INTO Melee VALUES('Kaedweni Militia', 'Medium Armoured Infantry', '2', 2, 0, 5, '6 armies', 'None')");
		db.execSQL("INSERT INTO Melee VALUES('Kaedweni Serzhant', 'Heavily Armoured Infantry', '2', 2, 0, 15, '2 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+1 Strength to 3 allied infantry units<br>&#8226; " +
				"An additional +1 Strength to these 3 units if they are attacking Heavily Armoured divisions')");
		db.execSQL("INSERT INTO Melee VALUES('Temerian Knight', 'Heavy Cavalry', '2.5', 3, 0, 12, '6 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength Bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Temerian Light Cavalry', 'Light Cavalry', '0.5', 4, 0, 5, '6 armies', '<br>&#8226; May perform Charge with +0.5 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+3 Initiative against Ranged units') ");
		db.execSQL("INSERT INTO Melee VALUES('Temerian Man-At-Arms', 'Heavily Armoured Infantry', '2', 2, 0, 10, '6 armies', '<br>&#8226; 2 Armour')");
		db.execSQL("INSERT INTO Melee VALUES('Temerian Conscript', 'Medium Polearm Infantry', '1', 2, 0, 6, '8 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Poor Fucking Infantry', 'Peasant Militia', '0.5', 2, 0, 3, '4 armies', '<br>&#8226; +0.5 Strength against Cavalry<br>&#8226; " +
				"Up to 50 PFI have no need for Maintenance, and thus are excluded from any calculation of Unit Maintenance.') ");
		db.execSQL("INSERT INTO Melee VALUES('Blue Stripes', 'Special Forces', '4', 3, 2, 20, '2 armies', '<br>&#8226; Survivability<br>&#8226; " +
				"Can only be recruited in @Territory Description@Vizima@<br>&#8226; " +
				"May perform Ambush<br>&#8226; " +
				"When Ambushing in the Default Temerian territories, it gains the Safe Retreat ability for 3 Combat Rounds.<br>&#8226; " +
				"Cannot be Focus-Fired or Ambushed<br>&#8226; " +
				"+2 Strength bonus against Scoia’tael units')");
		db.execSQL("INSERT INTO Melee VALUES('Redanian Knight', 'Heavy Cavalry/Lancer', '2.5', 3, 0, 13, '6 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+1 Strength bonus vs Other Cavalry (Bonus is halved when Charging, for a total of +1.5 Strength)<br>&#8226; " +
				"May perform Charge with +1 Strength Bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Redanian Scout Cavalry', 'Light Cavalry', '0.5', 4, 0, 5, '6 armies', '<br>&#8226; May perform Charge with +0.5 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+3 Initiative against Ranged units') ");
		db.execSQL("INSERT INTO Melee VALUES('Redanian Elite', 'Heavily Armoured Infantry', '2', 2, 0, 12, '6 armies', '<br>&#8226; 3 Armour')");
		db.execSQL("INSERT INTO Melee VALUES('Redanian Halberdier', 'Heavy Polearm Infantry', '1', 2, 0, 8, '8 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+1 Strength against cavalry')");
		db.execSQL("INSERT INTO Melee VALUES('Witch Hunter', 'Heavily Armoured Infantry', '1', 2, 0, 6, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+3 Initiative bonus against Mages<br>&#8226; " +
				"Can focus-fire on Mages<br>&#8226; " +
				"May waste a turn to throw a Dimeritium Bomb, at either the allies’ or the enemies’ side, canceling any magic effects and preventing any new ones from taking place for <i>n</i> combat rounds, where <i>n</i> = number of units divided by 5. Cooldown is 2 turns after the effect finishes.')");
		db.execSQL("INSERT INTO Melee VALUES('Redanian Free Company', 'Light Cavalry', '2', 4, 0, 12, '2 armies', '<br>&#8226; If Olgierd is in command, retain their positive cavalry attributes even when attacking Fortifications and Cities.<br>&#8226; " +
				"May Perform Charge with +1.0 Strength bonus<br>&#8226; " +
				"May Perform Maneuver<br>&#8226; " +
				"+3 Initiative against ranged units<br>&#8226; " +
				"If enemy forces outnumber allied ones in the battlefield, negate any cavalry-related penalty affecting them.') ");
		db.execSQL("INSERT INTO Melee VALUES('Tretogor Elite Troll Unit', 'Ogroid', '12 (3 Dice x 4 Strength)', 2, 0, 60, '1 army', '<br>&#8226; 10 Armour<br>&#8226; " +
				"Can only be recruited in @Territory Description@Tretogor@<br>&#8226; " +
				"5 Hit Points<br>&#8226; " +
				"May perform \"Shield Ally\" for 4 allied units')");
		db.execSQL("INSERT INTO Melee VALUES('Nausicaa Cavalry', 'Heavy Cavalry', '3', 3, 0, 18, '4 armies', '<br>&#8226; 3 armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus<br>&#8226; " +
				"+1 Stength against Heavily Armoured Infantry (bonus is halved when Charging)') ");
		db.execSQL("INSERT INTO Melee VALUES('Venendal Cavalry', 'Light Cavalry', '0.5', 4, 0, 6, '6 armies', '<br>&#8226; May perform Charge with +1 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+3 Initiative against Ranged units') ");
		db.execSQL("INSERT INTO Melee VALUES('Alba Division', 'Heavy Polearm Infantry', '1', 2, 0, 6, '8 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against cavalry')");
		db.execSQL("INSERT INTO Melee VALUES('Impera Brigade', 'Heavy Polearm Infantry', '2', 2, 0, 15, '3 armies', '<br>&#8226; 3 Armour<br>&#8226; " +
				"+1 Strength against cavalry<br>&#8226; " +
				"Can perform Shield Ally, for 3 allied units')");
		db.execSQL("INSERT INTO Melee VALUES('Daerlan Foot Soldier', 'Heavily Armoured Infantry', '2', 2, 0, 10, '6 armies', '<br>&#8226; 2 Armour')");
		db.execSQL("INSERT INTO Melee VALUES('Magne Division', 'Light Infantry', '1', 3, 0, 6, '2 armies', '<br>&#8226; +1 Strength in Desert territories')");
		db.execSQL("INSERT INTO Melee VALUES('Slave Infantry', 'Light Infantry', '0.5', 2, 0, 2, '8 armies', 'None')");
		db.execSQL("INSERT INTO Melee VALUES('Imperial Golem', 'Construct', '12 (4 Dice x 3 Strength)', 2, 0, 60, '1 army', '<br>&#8226; 5 Hit Points<br>&#8226; " +
				"8 Armour<br>&#8226; " +
				"Can only be recruited in @Territory Description@Nilfgaard@, @Territory Description@Xarthisius’ Tower@, @Territory Description@Vicovaro Academy@ and @Territory Description@Academy of Magic@<br>&#8226; " +
				"Immune to Magic -that includes allied and enemy spells, and mages’ ranged attacks-<br>&#8226; " +
				"Absorbs D4 Magic damage each Combat Round')");
		db.execSQL("INSERT INTO Melee VALUES('Cintrian Knight', 'Heavy Cavalry', '2.5 (2, plus 1 for every 2 units)', 3, 0, 12, '4 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Cintrian Longswordsman', 'Heavily Armoured Infantry', '2', 2, 0, 10, '3 armies', '<br>&#8226; 3 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Cintrian Pikeman', 'Light Polearm Infantry', '1', 2, 0, 6, '5 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('An Craite Raider', 'Light Infantry', '1.2 (1, plus 1 for every 5 units)', 3, 0, 5, '4 armies', '<br>&#8226; May perform Pillage<br>&#8226; " +
				"Pillaging costs only 1 Speed<br>&#8226; " +
				"Gains +50% gold when Pillaging') ");
		db.execSQL("INSERT INTO Melee VALUES('An Craite Greatswordsman', 'Medium Infantry', '2', 2, 0, 6, '2 armies', '<br>&#8226; +1 Strength against Polearm Infantry') ");
		db.execSQL("INSERT INTO Melee VALUES('An Craite Warcrier', 'Light Infantry/Bard', '0.5 (1 for every 2 units)', 2, 0, 11, '2 armies', '<br>&#8226; -1 Strength to 5 <i>Enemy</i> units') ");
		db.execSQL("INSERT INTO Melee VALUES('Drummond Warmonger', 'Heavy Infantry', '2', 2, 0, 9, '2 armies', '<br>&#8226; 2 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Drummond Shieldmaiden', 'Medium Infantry/Shieldunit', '1', 2, 0, 6, '1 armies', '<br>&#8226; 2 Armour against Ranged attacks<br>&#8226; " +
				"Negates any Ranged anti-armour bonus damage (like from enemy Crossbowmen) <br>&#8226; " +
				"May perform Shield Ally with a +3 Initiative, for 2 allied units') ");
		db.execSQL("INSERT INTO Melee VALUES('Dimun Pirate', 'Buccaneer Infantry', '1', 2, 0, 7, '4 armies', '<br>&#8226; +1 Strength and +1 Initiative when Boarding<br>&#8226; " +
				"May perform Pillage (on land), Plunder (on water), but not Desecrate. Look in the Pillaging ability section for more info.<br>&#8226; " +
				"Gains 50% more gold (rounded down) when Plundering') ");
		db.execSQL("INSERT INTO Melee VALUES('Dimun Corsair', 'Buccaneer Infantry', '2', 2, 0, 12, '2 armies', '<br>&#8226; +2 Strength and Initiative when Boarding<br>&#8226; " +
				"Also gives +1 Strength to the ship he’s on, and it cannot be focus-fired<br>&#8226; " +
				"May perform Pillage and Plunder, but not Desecrate<br>&#8226; " +
				"Gains +50% more gold when Plundering') ");
		db.execSQL("INSERT INTO Melee VALUES('Heymaey Battlemaiden', 'Light Polearm Infantry', '1', 2, 0, 5, '3 armies', '<br>&#8226; +1 Strength against Cavalry<br>&#8226; " +
				"+1 Strength against all monsters, and +1 additional Strength against Beasts (bears, wolves, etc.)') ");
		db.execSQL("INSERT INTO Melee VALUES('Tuisreach Axeman', 'Heavy Infantry', '2', 2, 0, 8, '4 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"Ignores Armour on Heavy Units') ");
		db.execSQL("INSERT INTO Melee VALUES('Vildkaarl', 'Light Infantry/Cursed', '2', 3, 0, 30, '2 armies', '<br>&#8226; 2 Armour against ranged attacks<br>&#8226; " +
				"Negates any Ranged anti-armour bonus damage (like from enemy Crossbowmen)<br>&#8226; " +
				"May perform Pillage and Desecrate, but not Plunder<br>&#8226; " +
				"After 1 combat round, transforms into Enraged Bear (5 Hit Points, 4 Strength, 3 Speed, 0 Initiative, 2 Armour, Immune to any form of negative effect concerning their attack, armour, movement or initiative).') ");
		db.execSQL("INSERT INTO Melee VALUES('Koviri Paladino', 'Heavy Cavalry', '2.5 (2, plus 1 for every 2 units)', 3, 0, 13, '2 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Koviri Picchierre', 'Medium Polearm Infantry', '1', 2, 0, 8, '4 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+2 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Koviri Condottiero', 'Medium Infantry', '2', 2, 0, 9, '4 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Light Infantry<br>&#8226; " +
				"+1 Initiative when Boarding<br>&#8226; " +
				"After being Recruited, can be sold to factions <i>allied to Kovir & Poviss</i> for 12 gold') ");
		db.execSQL("INSERT INTO Melee VALUES('Adieu’s Free Company', 'Heavily Armoured Infantry', '4', 2, 0, 22, '2 armies', '<br>&#8226; 4 Armour<br>&#8226; " +
				"Can only be recruited in @Territory Description@Tridam@<br>&#8226; " +
				"Negates the doubled anti-Armour damage of enemy units (like Crossbowmen), so they deal normal damage. Opposing units that would completely ignore Armour (like Maulers, Axemen or Siege) will instead deal double damage.') ");
		db.execSQL("INSERT INTO Melee VALUES('Hengforsian Cataphract', 'Heavy Cavalry', '2', 3, 0, 20, '2 armies', '<br>&#8226; 2 armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus. When Charging against foot soldiers, deals D10 Trample damage for every 8 Cataphracts performing the Charge (rounded down)<br>&#8226; " +
				"Negates up to 2 incoming anti-Cavalry Strength bonus from enemy units<br>&#8226; " +
				"+1 Stength against Heavily Armoured Infantry (bonus is halved when Charging)') ");
		db.execSQL("INSERT INTO Melee VALUES('Hengforsian Halberdier', 'Medium Polearm Infantry', '1', 2, 0, 6, '3 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Caingornian Swordsman', 'Heavily Armoured Infantry', '2', 2, 0, 10, '3 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+1 Strength when fighting in the default Hengforsian territories') ");
		db.execSQL("INSERT INTO Melee VALUES('Royal Mercenary Guard', 'Heavily Armoured Polearm Infantry', '3', 2, 0, 20, '1 army', '<br>&#8226; 3 Armour<br>&#8226; " +
				"+3 Strength against Cavalry<br>&#8226; " +
				"Deals double damage against armoured units<br>&#8226; " +
				"+1 Strength and Initiative when Boarding') ");
		db.execSQL("INSERT INTO Melee VALUES('Temple Guard', 'Heavily Armoured Infantry', '2', 2, 0, 11, '2 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+2 Strength against Armoured Infantry<br>&#8226; " +
				"Deals double damage against armoured units') ");
		db.execSQL("INSERT INTO Melee VALUES('Order of the Flaming Rose Zealot', 'Medium Infantry', '2', 2, 0, 10, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"If any allied Priests of the Eternal Fire are on the field, receives +1 Strength<br>&#8226; " +
				"+2 Armour against ranged attacks<br>&#8226; " +
				"Negates any Ranged anti-armour bonus damage (like from enemy Crossbowmen)') ");
		db.execSQL("INSERT INTO Melee VALUES('Vrihedd Dragoon', 'Light Cavalry', '1', 4, 0, 8, '3 armies', '<br>&#8226; +3 Initiative bonus against Ranged units<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Vrihedd Vanguard', 'Medium Cavalry', '2', 4, 0, 10, '3 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Dol Blathanna Defender', 'Medium Polearm Infantry/ Shieldunit', '1', 2, 0, 6, '3 armies', '<br>&#8226; Agility<br>&#8226; " +
				"May perform Shield Ally, for 1 allied unit<br>&#8226; " +
				"+1 Strength bonus against Cavalry<br>&#8226; " +
				"+2 Armour against Ranged attacks<br>&#8226; " +
				"Negates any Ranged anti-armour bonus damage (like from enemy Crossbowmen)') ");
		db.execSQL("INSERT INTO Melee VALUES('Elven Wardancer', 'Light Infantry', '2', 2, 0, 7, '5 armies', '<br>&#8226; Agility<br>&#8226; " +
				"May perform Dancing Whirlwind, gaining 6 Strength (3 Dice x 2 Strength)') ");
		db.execSQL("INSERT INTO Melee VALUES('Elven Deadeye', 'Special Forces', '4', 3, 2, 25, '1 army', '<br>&#8226; May perform Ambush<br>&#8226; " +
				"Cannot be Focus-Fired or Ambushed<br>&#8226; " +
				"+2 Strength against Humans<br>&#8226; " +
				"+2 Strength when fighting in Forests<br>&#8226; " +
				"May waste an action to Mark a unit or division. On subsequent turns, when the Deadeye attack their Marked target, they will attack twice. (stacks with Iorveth’s ability or any kind of bonus). Only one group of units can be Marked at a time. The Deadeye can spend an action or its whole movement to move their mark to another unit or group.') ");
		db.execSQL("INSERT INTO Melee VALUES('Mahakam Defender', 'Heavily Armoured Spearunit', '2', 2, 0, 11, '1-2 armies', '<br>&#8226; 3 Armour<br>&#8226; " +
				"+1 Strength bonus against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Mahakam Axeman', 'Medium Armoured Infantry', '2', 2, 0, 8, '1-2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Infantry<br>&#8226; " +
				"An additional +1 Strength when fighting in Mahakam or in territories two tiles away from it') ");
		db.execSQL("INSERT INTO Melee VALUES('Mahakam Marauder', 'Heavily Armoured Infantry', '3', 2, 0, 15, '1-2 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"Deals double damage to Light Infantry/Spearunits<br>&#8226; " +
				"Gains +1 Strength against Medium Infantry/Spearunits<br>&#8226; " +
				"Negates the Armour of Heavy Infantry/Spearunits') ");
		db.execSQL("INSERT INTO Melee VALUES('Bruggian Lancer', 'Medium Cavalry/Lancer', '1', 3, 0, 6, '2 armies', '<br>&#8226; +1 Strength against Cavalry<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Bruggian Landsknecht', 'Heavy Infantry', '3', 2, 0, 11, '2 armies', '<br>&#8226; 2 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Soddener Man-at-Arms', 'Heavy Infantry', '2', 2, 0, 10, '2 armies', '<br>&#8226; 2 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Soddener Axeman', 'Medium Infantry', '2', 2, 0, 10, '2 armies', '<br>&#8226; +1 Strength against Heavy Units<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		db.execSQL("INSERT INTO Melee VALUES('Free Slopes Lancer', 'Medium Cavalry', '1', 3, 0, 10, '2 armies', '<br>&#8226; Cannot be recruited by normal means, except from the specific event cards.<br>&#8226; " +
				"May perform Charge with +1 Strength<br>&#8226; " +
				"+1 Strength against Cavalry (stacks with Charge bonus)<br>&#8226; " +
				"When fighting inside the default Sodden territories, or when defending a Temerian or Cintrian City, this unit gains +1 Strength.') ");
		db.execSQL("INSERT INTO Melee VALUES('Free Slopes Crusher', 'Heavy Infantry', '4 (2 Dice x 2 Strength)', 2, 0, 16, '1 army', '<br>&#8226; Cannot be recruited by normal means, except from the specific event cards.<br>&#8226; " +
				"1 Armour<br>&#8226; " +
				"Ignores the Armour of Armoured Units<br>&#8226; " +
				"Deals triple damage to Armoured monsters<br>&#8226; " +
				"When fighting inside the default Sodden territories, or when defending a Temerian or Cintrian City, this unit deals double damage to Infantry.') ");
		db.execSQL("INSERT INTO Melee VALUES('Cidarian Companion', 'Light Cavalry/Lancer', '1', 4, 0, 8, '2 armies', '<br>&#8226; +1 Strength against Light Cavalry<br>&#8226; " +
				"+0.5 Strength against other types of Cavalry<br>&#8226; " +
				"+3 Initiative against Ranged units<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Cidarian Hoplite', 'Medium Infantry', '2', 2, 0, 6, '2 armies', '<br>&#8226; 1 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Cidarian Peltast', 'Light Spearunit/Ranged', '1', 2, 0, 4, '2 armies', '<br>&#8226; May perform a Ranged attack with +1 Initiative and 1 Range. After doing so, the Peltast cannot attack in Melee in the same turn but can still move with its normal initiative, if  there’s any Speed left.<br>&#8226; " +
				"+1 Strength against Cavalry when performing a melee attack<br>&#8226; " +
				"+1 Strength against Armoured Infantry when performing a ranged attack') ");
		db.execSQL("INSERT INTO Melee VALUES('Kerackian Huskarl', 'Heavy Infantry', '2', 2, 0, 10, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+3 Armour against Ranged attacks<br>&#8226; " +
				"+1 damage to Armoured units and Armoured monsters<br>&#8226; " +
				"+1 Strength when boarding<br>&#8226; " +
				"May perform Pillage and Plunder') ");
		db.execSQL("INSERT INTO Melee VALUES('Kerackian Raider', 'Medium Infantry', '2', 2, 0, 8, '2 armies', '<br>&#8226; +1 Speed when boarding<br>&#8226; " +
				"In the first turn of combat, it gains +1 Speed (does not stack with the boarding bonus).<br>&#8226; " +
				"Ignores Armour on Armoured Units<br>&#8226; " +
				"May perform Pillage and Plunder') ");
		db.execSQL("INSERT INTO Melee VALUES('Kerackian Spearman', 'Medium Infantry', '1', 2, 0, 6, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Conscript', 'Medium Infantry', '2', 2, 0, 6, '2 armies', '<br>&#8226; 1 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Pikeman', 'Light Polearm Infantry', '1', 2, 0, 5, '2 armies', '<br>&#8226; +1 Strength against Cavalry<br>&#8226; " +
				"+1 additional Strength against Heavy Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Royal Dragoon', 'Heavy Cavalry', '3', 3, 0, 15, '1 army', '<br>&#8226; 2 Armour<br>&#8226; " +
				"May perform Charge with +2 Strength bonus<br>&#8226; " +
				"+2 Strenght against retreating units') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Royal Guard', 'Heavy Infantry', '3', 2, 0, 15, '1 army', '<br>&#8226; 3 Armour<br>&#8226; " +
				"+2 Strength against Guerilla/Ambush units') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Guerilla Ambusher', 'Light Guerilla Infantry', '1', 3, 3, 15, '1 or 2 armies, for a total of 5 guerilla armies', '<br>&#8226; May perform Ambush when inside the default Verden territories<br>&#8226; " +
				"May Hide when inside the default Verden and Brokilon territories. When doing so, it cannot attack for that turn but it’s completely invulnerable to damage from any kind of unit, except from Special Forces or other Guerilla units.<br>&#8226; " +
				"When performing Ambush, gain +4 Strength for 3 turns') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Guerilla Bomber', 'Light Guerilla Infantry', '12 (6 Dice x 2 Strength)', 3, 3, 35, '1 or 2 armies, for a total of 5 guerilla armies', '<br>&#8226; May perform Ambush when inside the default Verden territories<br>&#8226; " +
				"May Hide when inside the default Verden territories. When doing so, it cannot attack for that turn but it’s completely invulnerable to damage from any kind of unit, except from Special Forces or other Guerilla units.<br>&#8226; " +
				"Does not gain the Dual-Action when performing an Ambush. Instead, it gains double movement for the first 3 rounds of the Ambush.<br>&#8226; " +
				"May attack enemy Siege or Fortifications with 8 Strength (4 Dice x 2 Strength)') ");
		db.execSQL("INSERT INTO Melee VALUES('Verdenian Guerilla Trapper', 'Light Guerilla Infantry', '1', 3, 3, 45, '1 or 2 armies, for a total of 5 guerilla armies', '<br>&#8226; May perform Ambush when inside the default Verden territories<br>&#8226; " +
				"May Hide when inside the default Verden territories. When doing so, it cannot attack for that turn but it’s completely invulnerable to damage from any kind of unit, except from Special Forces or other Guerilla units.<br>&#8226; " +
				"May waste an action and 3 Speed to place a trap on his current Row. Each group of Trappers can only trap one row. If the Trappers want to place another trap in another turn, they will need to disarm the trap, which costs an action and 1 Speed. The trap can harm (D8+2) units, dealing 1 damage, and if they are Heavy units, or Charging Cavalry of any kind, they are damaged before attacking.<br>&#8226; " +
				"If enemy melee units attack allied units on that Row the trap was placed, they are damaged by the trap. If enemy Medium or Heavy Infantry/Cavalry try to cross the Trapper’s Row where a trap was placed, they are damaged as well. Note that there’s a limited amount of units that can be harmed each turn (basically, it’s n*[D8+2], where n = the number of Trappers that place a trap)<br>&#8226; " +
				"When performing an Ambush, on the first combat round, the Trappers can freely place a trap in any row and then move to a completely different row, as if they have unlimited movement. This however consumes the Dual-Action bonus of the Ambush.') ");
		db.execSQL("INSERT INTO Melee VALUES('Angrenian Horseman', 'Heavy Cavalry', '2.5 (2 plus 1 for every 2 units)', 3, 0, 11, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"May perform Charge with +1 Strength bonus') ");
		db.execSQL("INSERT INTO Melee VALUES('Angrenian Protector', 'Heavy Polearm Infantry', '1', 2, 0, 8, '2 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+1 Strength against Cavalry') ");
		db.execSQL("INSERT INTO Melee VALUES('Angrenian Claimhdean', 'Medium Infantry', '2', 2, 0, 6, '2 armies', '<br>&#8226; +1 Strength against Polearm Infantry<br>&#8226; " +
				"When moving or fighting in the default Angren territories, gains +1 Speed and +1 Strength boni.') ");
		db.execSQL("INSERT INTO Melee VALUES('Beauclairoise Knight-Errant', 'Heavy Cavalry', '3', 3, 0, 25, '2 armies', '<br>&#8226; 4 Armour<br>&#8226; " +
				"May perform charge with +2 Strength<br>&#8226; " +
				"+2 Strength when fighting in the default Toussaintoise territories<br>&#8226; " +
				"+3 Strength against monsters and bandits<br>&#8226; " +
				"Deals double damage to Armoured monsters') ");
		db.execSQL("INSERT INTO Melee VALUES('Beauclairoise Ducal Guard', 'Heavy Polearm Infantry', '2', 2, 0, 12, '3 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"+2 Strength against Cavalry') ");
		// missing a lot
		db.execSQL("INSERT INTO Melee VALUES('Zerrikanian Swordsman', 'Light Infantry', '1', 2, 0, 4, '4 armies', '<br>&#8226; +1 Strength bonus against Light Infantry<br>&#8226; " +
				"+1 Strength bonus in Desert biome') ");
		db.execSQL("INSERT INTO Melee VALUES('Zerrikanian Cavalry', 'Light Cavalry', '0.5', 5, 0, 9, '4 armies', '<br>&#8226; May perform Charge with +1 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+3 Initiative and +0.5 Strength against Ranged units<br>&#8226; " +
				"Unaffected by Desert and Tundra biome penalties') ");
		db.execSQL("INSERT INTO Melee VALUES('Zerrikanian Camelier', 'Light Camelry', '1', 4, 0, 15, '4 armies', '<br>&#8226; May perform Charge with +1 Strength bonus<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"+2 Strength bonus against Cavalry<br>&#8226; " +
				"Negates enemy anti-cavalry Strength bonus<br>&#8226; " +
				"+2 Strength in Desert biome, unaffected by Tundra biome penalties') ");
		db.execSQL("INSERT INTO Melee VALUES('Zerrikanian Mamluk', 'Heavy Camelry', '3', 3, 0, 30, '2 armies', '<br>&#8226; 4 Armour<br>&#8226; " +
				"+3 Strength bonus against Cavalry<br>&#8226; " +
				"+2 Strength bonus against Infantry<br>&#8226; " +
				"Negates enemy anti-cavalry and anti-armour Strength boni (enemy units that Double their damage, like Crossbowmen, will still do, just without receiving the bonus strength, while units that completely Ignore Armour, like Siege, will instead deal double damage, like Crossbowmen).<br>&#8226; " +
				"+3 Strength in Desert biome, unaffected by Tundra biome penalties') ");
		// missing Haakland
		db.execSQL("INSERT INTO Melee VALUES('Red Rider', 'Heavy Cavalry', '3', 3, 0, 0, '3 armies', '<br>&#8226; May perform Charge with +2 Strength bonus<br>&#8226; " +
				"3 Armour<br>&#8226; " +
				"+2 Strength against Humans') ");
		db.execSQL("INSERT INTO Melee VALUES('Wild Hunt Warrior', 'Heavy Infantry', '2', 2, 0, 0, '3 armies', '<br>&#8226; 2 Armour<br>&#8226; " +
				"Deals double damage to Armoured units') ");
		db.execSQL("INSERT INTO Melee VALUES('Wild Hunt Hound', 'Elementa', '1', 3, 0, 0, '3 armies', '<br>&#8226; 2 Hit Points<br>&#8226; " +
				"Once per battle it can summon Frost Spikes around it, with 4 initiative, dealing Ice damage to every unit stack that is engaged in combat with it. The damage is (2D8+8) +2 for each Hound, and it is dealt to each engaged division. Damaged units are frozen for 1 turn, being unable to act and receiving double damage from melee attacks. During Hailstorm or Blizzard this ability has a cooldown of 2 turns.') ");
		db.execSQL("INSERT INTO Melee VALUES('Stray Cavalry', 'Light Cavalry', '1', 4, 0, 5, '2 armies', '<br>&#8226; May perform Maneuver<br>&#8226; " +
				"+3 Initiative against Ranged units<br>&#8226; " +
				"Gains +50% gold when Pillaging') ");
		db.execSQL("INSERT INTO Melee VALUES('Stray Marauder', 'Medium Infantry', '2', 2, 0, 7, '2 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"Gains +50% gold when Pillaging') ");
		db.execSQL("INSERT INTO Melee VALUES('Salamandra Henchman', 'Heavy Infantry', '2', 2, 0, 9, '4 armies', '<br>&#8226; 2 Armour') ");
		db.execSQL("INSERT INTO Melee VALUES('Salamandra Mutant', 'Mutant', '16 (4 Dice x 4 Strength)', 2, 0, 50, '2 armies', '<br>&#8226; 6 Hit Points<br>&#8226; " +
				"Agility<br>&#8226; " +
				"May perform Dancing Whirlwind, (gaining D8+2) Strength.') ");
		db.execSQL("INSERT INTO Melee VALUES('Salamandra Rabid Hound', 'Beast', '2', 3, 0, 12, '4 armies', '<br>&#8226; 2 Hit Points<br>&#8226; " +
				"Pack Tactics: If 10 or more allied Rabid Hounds are present on the battlefield, they all gain +1 Strength.') ");
		// endregion

		//Create Ranged table
		db.execSQL("CREATE TABLE Ranged " +
				"( unitn TEXT PRIMARY KEY, specialization TEXT, strength TEXT, speed INTEGER, initiative INTEGER, range INTEGER, cost INTEGER, armylimit TEXT, abilities TEXT, FOREIGN KEY(unitn) REFERENCES Unit(unitn))");

		// region Insert Ranged entries
		db.execSQL("INSERT INTO Ranged VALUES('Aedirnian Longbowman', 'Archer', '1', 2, 2, 4, 7, '4 armies', '<br>&#8226; Can only attack using an active ability<br>&#8226; " +
				"Can perform Double-Shot, with default Strength<br>&#8226; " +
				"Can perform Precise-Shot, with +0.5 Strength<br>&#8226; " +
				"Can perform Scatter-Shot, with -1 Initiative') ");
		db.execSQL("INSERT INTO Ranged VALUES('Lyrian Arbalist', 'Crossbowman', '2', 2, 1, 2, 10, '4 armies', '<br>&#8226; When attacking Armoured units without Double-Shot, deals triple damage to them.<br>&#8226; " +
				"Can perform Double-Shot, with default Strength') ");
		db.execSQL("INSERT INTO Ranged VALUES('Onager', 'Ranged Siege', '15 (5 Dice x 3 Strength)', 1, 3, 6, 40, '2 armies', '<br>&#8226; Needs 2 Combat Rounds to set up<br>&#8226; " +
				"Can either attack units or fortifications with 4 Strength (1 Die x 4 Strength)<br>&#8226; " +
				"Can attack enemy siege with 6 Strength (3 Dice x 2 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Ballista', 'Ranged Siege', '12 (4 Dice x 3 Strength)', 1, 3, 6, 35, '2 armies', '<br>&#8226; Can focus-fire units even inside fortifications<br>&#8226; " +
				"Can attack enemy siege with 5 Strength (5 Dice x 1 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Kaedweni Bowman', 'Archer', '1', 2, 2, 3, 5, '6 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Trebuchet', 'Ranged Siege', '10 (2 Dice x 5 Strength)', 1, 3, 6, 40, '2 armies', '<br>&#8226; Needs 2 Combat Rounds to set up.<br>&#8226; " +
				"Can either attack units or fortifications with 6 Strength (1 Die x 6 Strength)<br>&#8226; " +
				"Can attack enemy siege with 6 Strength (2 Dice x 3 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Scorpion', 'Ranged Siege', '12 (3 Dice x 4 Strength)', 1, 3, 6, 35, '2 armies', '<br>&#8226; Can focus-fire units even inside fortifications<br>&#8226; " +
				"Can attack enemy siege with 5 Strength (5 Dice x 1 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Reinforced Trebuchet', 'Ranged Siege', '18 (3 Dice x 6 Strength)', 1, 4, 6, 60, '1 army', '<br>&#8226; Needs 2 Combat Rounds to set up<br>&#8226; " +
				"Can either attack units or fortifications with 12 Strength (2 Die x 6 Strength)<br>&#8226; " +
				"Can attack enemy siege with 9 Strength (3 Dice x 3 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Light Scorpion', 'Ranged Siege', '8 (2 Dice x 4 Strength)', 1, 3, 6, 20, '2 armies', '<br>&#8226; Can focus-fire units even inside fortifications<br>&#8226; " +
				"Can attack enemy siege with 3 Strength (3 Dice x 1 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Temerian Crossbowman', 'Crossbowman', '1', 2, 1, 2, 7, '6 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Redanian Crossbowman', 'Crossbowman', '1', 2, 1, 2, 7, '6 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Redanian Longbowman', 'Archer', '1', 2, 2, 4, 5, '6 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Impera Enforcer', 'Heavily Armoured Crossbowman', '1', 2, 1, 2, 15, '3 armies', '<br>&#8226; 1 Armour<br>&#8226; " +
				"+1 Strength against Heavy Infantry/ Heavy Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units.<br>&#8226; " +
				"Can Perform “Impera Testudo Formatio”') ");
		db.execSQL("INSERT INTO Ranged VALUES('Black Infantry Archer', 'Archer', '2', 2, 2, 4, 8, '6 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Zerrikanian Fire Scorpion', 'Ranged Siege', '24 (8 Dice x 3 Strength)', 1, 3, 6, 75, '1 army', '<br>&#8226; Can focus-fire units even inside fortifications<br>&#8226; " +
				"Non-focused attacks against cities and forts have a chance to start a fire, killing 2 more units per turn, for 3 turns.  Roll a D6 for each unit individually. For every 6 rolled, a fire has started.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Rot Tosser', 'Ranged Siege', '6 (3 Dice x 2 Strength)', 1, 3, 6, 50, '1 army', '<br>&#8226; Can only be used when besieging forts and cities<br>&#8226; " +
				"This unit can spread diseases inside enemy fortifications. After attacking with its 3x2 Strength, if it lands at least 1 hit, raise its Strength to 8 (4 Dice x 2 Strength). Keep raising its strength by 2, each round it lands a hit, to infinity and beyond.<br>&#8226; " +
				"Even after it dies in combat, the rotten carcasses it has thrown to the enemy will keep inflicting damage, for 2 more combat rounds -unless the battle is finished earlier-. This “after” effect, has a Strength equal to the Rot Tosser’s final Strength, minus 2. -So, if the Rot Tosser has 10 Strength (5 x 2) when it dies, the disease will have 8 Strength (4 x 2)-') ");
		db.execSQL("INSERT INTO Ranged VALUES('Cintrian Crossbowman', 'Crossbowman', '1', 2, 1, 2, 7, '4 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Brokvar Hunter', 'Archer', '1.5 (1, plus 1 for every 2 units)', 2, 2, 4, 8, '2 armies', '<br>&#8226; Hunter Eyes<br>&#8226; " +
				"+1 Initiative and +0.5 Strength when Boarding') ");
		db.execSQL("INSERT INTO Ranged VALUES('Whale Harpoon', 'Ranged Siege', '10 (5 Dice x 2 Strength)', 1, 3, 6, 35, '2 armies', '<br>&#8226; Can focus-fire units even inside fortifications<br>&#8226; " +
				"When embarked on a ship, consumes only half the space of a siege unit (so even 2 of them can be placed even on smaller ships)<br>&#8226; " +
				"Can attack enemy siege or ships (in naval battles) with 5 Strength (5 Dice x 1 Strength)<br>&#8226; " +
				"In the rare case of a naval battle against a gigantic sea monster, it attacks with full strength.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Koviri Mounted Arquebusier', 'Mounted Ranged Unit', '5', 3, 0, 1, 14, '2 armies', '<br>&#8226; May attack with +1 Initiative but -1 Strength') ");
		db.execSQL("INSERT INTO Ranged VALUES('Koviri Balestriere', 'Crossbowman', '1', 2, 1, 2, 8, '4 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Carambola', 'Ranged Siege', '14 [(1 Die x 6 Strength) + (4 Dice x 2 Strength)]', 1, 4, 6, 80, '1 army', '<br>&#8226; Needs 1 Combat Round to set up<br>&#8226; " +
				"Can either attack units, or fortifications with 12 Strength (4 Dice x 3 Strength)<br>&#8226; " +
				"Can attack enemy siege or ships (in naval battles) with 15 Strength (5 Dice x 3 Strength)') ");
		db.execSQL("INSERT INTO Ranged VALUES('Malleorean Longbowman', 'Archer', '1', 2, 2, 4, 5, '3 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Vrihedd Brigade', 'Light Cavalry Archer', '2', 4, 1, 2, 14, '3 armies', '<br>&#8226; May perform Hit and Run<br>&#8226; " +
				"May perform Maneuver<br>&#8226; " +
				"On each successful attack, this unit gets a free attack, but this time with default Strength and no added boni') ");
		db.execSQL("INSERT INTO Ranged VALUES('Dol Blathanna Archer', 'Archer', '2', 2, 2, 4, 7, '3 armies', '<br>&#8226; +1 Strength bonus when fighting in the default Dol Blathanna and Brokilon territories') ");
		db.execSQL("INSERT INTO Ranged VALUES('Elven Marksman', 'Archer', '2', 2, 2, 4, 9, '5 armies', '<br>&#8226; May perform Precise Shot against Heavily Armoured Units, with -0.5 Strength penalty. This attack negates their armour') ");
		db.execSQL("INSERT INTO Ranged VALUES('Dryad Ranger', 'Archer', 'D4', 3, 2, 4, 0, '2 armies', '<br>&#8226; Cannot be recruited by normal means, except from the specific event cards.<br>&#8226; " +
				"When inside the default territories of Brokilon, gains +D4 Strength and +1 Initiative boni, deals double damage to Armoured units, plus has the ability to perform Ambush and be immune to Ambush attempts.<br>&#8226; " +
				"Deals double damage to Humans.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Mahakam Skirmisher', 'Light Spearunit/Ranged', '1.5', 2, 0, 2, 7, '1-2 armies', '<br>&#8226; +0.5 Strength against Cavalry<br>&#8226; " +
				"+1.5 Strength and +1 Initiative against Crossbowmen') ");
		db.execSQL("INSERT INTO Ranged VALUES('Mahakam Arbalist', 'Crossbowman', '3 (3 Dice x 1 Strength)', 2, 1, 2, 10, '1-2 armies', '<br>&#8226; Deals double damage on Armoured Units<br>&#8226; " +
				"+2 Strength (2 Dice x 1 Strength) against Armoured Creatures (like golems, trolls, etc.), but does not deal double damage.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Bruggian Crossbowman', 'Crossbowman', '1', 2, 1, 2, 7, '2 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry<br>&#8226; " +
				"Deals double damage to Armoured Units') ");
		db.execSQL("INSERT INTO Ranged VALUES('Bruggian Slinger', 'Slinger', '1', 2, 1, 2, 5, '2 armies', '<br>&#8226; +2 Strength against Light Infantry<br>&#8226; " +
				"+1 Strength against Medium Infantry and Light Ranged Units') ");
		db.execSQL("INSERT INTO Ranged VALUES('Soddener Archer', 'Light Archer', '1', 2, 2, 3, 6, '2 armies', '<br>&#8226; Each turn that this unit doesn’t move, it may perform Double-Shot, with Default Strength.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Free Slopes Skirmisher', 'Peasant Militia', '1', 2, 1, 2, 5, '2 armies', '<br>&#8226; Cannot be recruited by normal means, except from the specific event cards.<br>&#8226; " +
				"Can either perform a melee or a ranged attack, all at 1 initiative, making it one of the few units that can attack in melee with a positive initiative.<br>&#8226; " +
				"Deals double damage to cavalry when using melee attacks.<br>&#8226; " +
				"Deals double damage to ranged units when using ranged attacks.<br>&#8226; " +
				"When fighting inside the default Sodden territories, or when defending a Temerian or Cintrian City, this unit can attack twice each turn.') ");
		db.execSQL("INSERT INTO Ranged VALUES('Cidarian Toxotai', 'Archer', '1', 2, 2, 4, 6, '2 armies', '<br>&#8226; +1 Strength against units at 2 tiles or less') ");
		db.execSQL("INSERT INTO Ranged VALUES('Kerackian Archer', 'Light Archer', '1', 2, 2, 3, 5, '2 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Verdenian Longbowman', 'Light Archer', '1', 2, 2, 4, 5, '2 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Angrenian Bowman', 'Light Archer', '1', 2, 2, 3, 5, '2 armies', 'None') ");
		db.execSQL("INSERT INTO Ranged VALUES('Toussaintoise Crossbowman', 'Crossbowman', '1', 2, 1, 2, 7, '3 armies', '<br>&#8226; +1 Strength against Heavy Infantry/Cavalry.<br>&#8226; " +
				"Deals double damage to Armoured Units.') ");
		// missing a lot
		db.execSQL("INSERT INTO Ranged VALUES('Zerrikanian Archer', 'Archer', '0.5', 2, 2, 3, 6, '4 armies', '<br>&#8226; +1.5 Strength when Defending a Zerrikanian Town/Fort/etc<br>&#8226; " +
				"+1 Initiative in Desert biome') ");
		db.execSQL("INSERT INTO Ranged VALUES('Zerrikanian Crossbowman', 'Crossbowman', '1', 2, 1, 2, 6, '4 armies', '<br>&#8226; +1 Strength against Heavily Armoured units<br>&#8226; " +
				"Deals double damage on Armoured Units<br>&#8226; " +
				"+1 Initiative in Desert biome') ");
		db.execSQL("INSERT INTO Ranged VALUES('Zerrikanian Camel Archer', 'Camelry Archer', '2', 4, 2, 3, 20, '3 armies', '<br>&#8226; May perform Maneuver<br>&#8226; " +
				"May perform Hit and Run.<br>&#8226; " +
				"+1 Strength bonus against Infantry.<br>&#8226; " +
				"+2 Strength bonus against Cavalry Archers.<br>&#8226; " +
				"Negates enemy anti-cavalry Strength boni, but not anti-archer ones.<br>&#8226; " +
				"After each successful hit, the Camel Archer gets a free attack, but this time with default Strength and no applied boni.') ");
		db.execSQL("INSERT INTO Ranged VALUES('War Elephant', 'Cavalry Artillery', 'D6', 2, 1, 3, 120, '1 army', '<br>&#8226; 36 Hit Points<br>&#8226; " +
				"Can also attack in melee, even after performing its ranged attack.<br>&#8226; " +
				"While in melee its Strength is D8+4, it negates Armour and attacks with 0 Initiative.<br>&#8226; " +
				"Receives double damage from enemy Spearunits and Camelry, but negates any other type of anti-cavalry Bonus.<br>&#8226; " +
				"Receives half-damage from enemy Ranged attacks.<br>&#8226; " +
				"When left with less than 10 Hit Points, the elephant may go berserk. Roll a D8 each turn (until the Elephant dies). If it rolls 5-8, the unit goes berserk, attacking anything close by, with double Strength (2D8+8).On the next turn, and each , the Rider may attempt to kill the elephant , rolling his usual D6 to attack it. If the damage is enough to kill it, the unit dies.<br>&#8226; " +
				"When out of combat, the War Elephant may pull an allied siege unit along, increasing its movement by 1.') ");
		// missing Haakland
		db.execSQL("INSERT INTO Ranged VALUES('Stray Slinger', 'Slinger', '1', 2, 1, 2, 5, '2 armies', '<br>&#8226; +2 Strength against Light and Medium Infantry') ");
		// endregion

		//Create Mage table
		db.execSQL("CREATE TABLE Mage " +
				"( unitn TEXT PRIMARY KEY, specialization TEXT, strength TEXT, essence INTEGER, speed INTEGER, initiative INTEGER, range INTEGER, cost INTEGER, armylimit TEXT, abilities TEXT, FOREIGN KEY(unitn) REFERENCES Unit(unitn))");

		// region Insert Mage entries
		db.execSQL("INSERT INTO Mage VALUES('Mage', 'Mage', '5', 15, 2, 2, 6, 40, '2 armies', '<br>&#8226; Restores 3 Essence each turn up to a maximum of 45 Essence<br>&#8226; " +
				"Teleportation<br>&#8226; " +
				"Generic Mage Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Ban Ard Tutor', 'Mage', '5', 30, 2, 2, 6, 60, '1 army', '<br>&#8226; Restores 4 Essence each turn up to a maximum of 60 Essence<br>&#8226; " +
				"Can only be recruited in @Territory Description@Ban Ard@<br>&#8226; " +
				"Teleportation<br>&#8226; " +
				"Ban Ard Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Aretuza Adept', 'Mage', '6', 30, 2, 2, 6, 70, '1 army', '<br>&#8226; Restores 5 Essence each turn up to a maximum of 75 Essence<br>&#8226; " +
				"Can only be recruited in @Territory Description@Thanedd Isle@<br>&#8226; " +
				"Teleportation<br>&#8226; " +
				"Aretuza Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Vicovaro Novice', 'Mage', '4', 15, 2, 2, 6, 40, '2 armies', '<br>&#8226; Restores 3 Essence each turn up to a maximum of 45 Essence<br>&#8226; " +
				"Can only be recruited in @Territory Description@Vicovaro Academy@ and @Territory Description@Academy of Magic@<br>&#8226; " +
				"Teleportation<br>&#8226; " +
				"Vicovaro Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Vaedermakar', 'Mage', '8 (2 Dice x 4 Strength)', 35, 2, 2, 6, 75, '2 armies', '<br>&#8226; Restores 4 Essence each turn up to a maximum of 60 Essence<br>&#8226; " +
				"Can cast spells and fight in a Naval battle<br>&#8226; " +
				"Vaedermakar Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Druid', 'Druid', '9 (3 Dice x 3 Strength)', 15, 2, 2, 6, 50, '2 armies', '<br>&#8226; Restores 3 Essence each turn up to a maximum of 45 Essence<br>&#8226; " +
				"Druid Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Koviri Magister', 'Mage', '4', 30, 2, 2, 6, 80, '2 armies', '<br>&#8226; Teleportation<br>&#8226; " +
				"Can only be recruited in @Territory Description@Lan Exeter@ and @Territory Description@Pont Vanis@<br>&#8226; " +
				"Does not restore Essence each turn<br>&#8226; " +
				"May waste a Spell Action to restore (8n+8+D8) Essence, where <i>n</i> is the amount of Magisters in the army, minus their leader/commander, up to a maximum of 90 Essence<br>&#8226; " +
				"Lan Exeter Spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Dryad Grovekeeper', 'Healer', '14 (7 Dice x 2 Strength)', 15, 3, 2, 6, 0, '1 army', '<br>&#8226; Cannot be recruited by normal means, except from the specific event cards.<br>&#8226; " +
				"Each turn, instead of attacking, she can heal D4 allied Dryads, but on -2 Initiative.<br>&#8226; " +
				"Restores 3 Essence each turn up to a maximum of 45 Essence<br>&#8226; " +
				"When inside the default territories of Brokilon, restores 6 Essence each turn instead.<br>&#8226; " +
				"Contrary to other mage and/or support units, the Grovekeepers are Bronze units. Note however that while inside the sacred oaks of Brokilon, and as long as the forrest is protected, the Dryads receive only half damage.<br>&#8226; " +
				"Duén Canell spellbook')");
		db.execSQL("INSERT INTO Mage VALUES('Druid of the Black Grove', 'Druid', '9 (3 Dice x 3 Strength)', 15, 2, 2, 6, 50, '1 army', '<br>&#8226; Restores 3 Essence each turn up to a maximum of 45 Essence<br>&#8226; " +
				"Caed Dhu spellbook')");
		// missing a lot
		db.execSQL("INSERT INTO Mage VALUES('Zerrikanian Fire Warlock', 'Mage', '5', 20, 2, 2, 6, 50, '3 armies', '<br>&#8226; Teleportation<br>&#8226; " +
				"Restores 10 Essence each turn that doesn’t cast a spell up to a maximum of 90 Essence.<br>&#8226; " +
				"Zerrikanian Spellbook')");
		// missing Haakland
		db.execSQL("INSERT INTO Mage VALUES('Wild Hunt Navigator', 'Mage', '5', 20, 2, 2, 6, 0, '2 armies', '<br>&#8226; Teleportation<br>&#8226; " +
				"Restores 4 Essence each turn up to a maximum of 60 Essence.<br>&#8226; " +
				"Dearg Ruadhri Spellbook')");
		// endregion

		//Create Support table
		db.execSQL("CREATE TABLE Support " +
				"( unitn TEXT PRIMARY KEY, specialization TEXT, speed INTEGER, initiative INTEGER, cost INTEGER, armylimit TEXT, abilities TEXT, FOREIGN KEY(unitn) REFERENCES Unit(unitn))");

		// region Insert Support entries
		db.execSQL("INSERT INTO Support VALUES('Rivian Warcrier', 'Bard', 2, 2, 15, '2 armies', '<br>&#8226; Gives +0.5 Strength to 10 allied units<br>&#8226; " +
				"This bonus changes to +1 if the aforementioned units are either attacking or being attacked by units with a bonus against them. -for instance, if a handful of Lyrian Huszárok is attacking enemy Spearmen, it gets +1 bonus, instead of +0.5-')");
		db.execSQL("INSERT INTO Support VALUES('Field Medic', 'Healer', 2, -2, 40, '1 army', '<br>&#8226; Can resurrect D4 bronze units every turn<br>&#8226; " +
				"In non-siege battles, after the 1st combat round, this unit has a chance to get damaged in the crossfire by non-focused attacks. For each Field Medic, roll a D10. If it rolls 1, it dies.<br>&#8226; " +
				"In Siege battles, the defender shall only roll D10 for the incoming non-focused ranged/siege attacks. For melee/spell/etc. attacks, roll a D20 instead. Again, if it rolls 1, a medic dies.<br>&#8226; " +
				"After the battle is over, the player controlling the battle site can resurrect (D4 – 1) bronze units for each medic.')");
		db.execSQL("INSERT INTO Support VALUES('Spy', 'Diplomat', 4, 0, 10, '2 armies', '<br>&#8226; Cannot engage in combat, die or get captured by enemy units<br>&#8226; " +
				"May perform Espionage and Counter-Espionage<br>&#8226; " +
				"May perform Diplomacy with Players’ or NPC’s parties')");
		db.execSQL("INSERT INTO Support VALUES('Kaedweni Siege Master', 'Siege Support', 2, -2, 40, '2 army', '<br>&#8226; Can Resurrect 1 allied Siege Unit each turn<br>&#8226; " +
				"After the 1st Combat Round, if the allied siege line is attacked by enemy siege, this unit has a chance to get damaged in the crossfire. For each Siege Master, roll a D12. If it rolls 1, it dies.')");
		db.execSQL("INSERT INTO Support VALUES('Temerian Drummer', 'Bard', 2, 2, 15, '2 armies', '<br>&#8226; Gives +1 Strength to 5 allied units<br>&#8226; " +
				"Cannot be Focus-Fired')");
		db.execSQL("INSERT INTO Support VALUES('Nilfgaardian Alchemist', 'Support', 2, 3, 22, '2 armies', '<br>&#8226; May brew Thunderbolt potion (+1 Strength to 8 allied melee units, for 1 turn)<br>&#8226; " +
				"May brew Hanged Man’s Venom (+1 Strength to 8 allied ranged units, for 1 turn)<br>&#8226; " +
				"May brew Dragon’s Dream (+3 Strength to 3 allied @Unit Description@Onager@s or @Unit Description@Ballista@, for 1 turn. Bonus is only applied when attacking enemy units, not siege or fortifications)<br>&#8226; " +
				"Each of these actions waste a combat round')");
		db.execSQL("INSERT INTO Support VALUES('Vicovaro Medic', 'Healer', 2, -2, 75, '2 armies', '<br>&#8226; Can only be recruited in @Territory Description@Vicovaro Academy@<br>&#8226; " +
				"Can resurrect (D4+2) bronze units every turn<br>&#8226; " +
				"In non-siege battles, after the 1st combat round, this unit has a chance to get damaged in the crossfire by non-focused attacks. For each Vicovaro Medic, roll a D10. If it rolls 1, it dies.<br>&#8226; " +
				"In Siege battles, the defender shall only roll D10 for the incoming non-focused ranged/siege attacks. For melee/spell/etc. attacks, roll a D20 instead. Again, if it rolls 1, a medic dies.<br>&#8226; " +
				"After the battle is over, the player controlling the battle site can resurrect (D6+1) bronze units for each medic.')");
		db.execSQL("INSERT INTO Support VALUES('Combat Engineer', 'Siege Support', 2, -2, 50, '2 armies', '<br>&#8226; Can Resurrect 1 allied Siege Unit each turn<br>&#8226; " +
				"After the 1st Combat Round, if the allied siege line is attacked by enemy siege, this unit has a chance to get damaged in the crossfire. For each Combat Engineer, roll a D8. If it rolls 1, it dies.')");
		db.execSQL("INSERT INTO Support VALUES('Heymaey Skjald', 'Bard', 2, 6, 14, '2 armies', '<br>&#8226; Before the combat round even starts, roll a D8<br>&#8226; " +
				"Give +1 Strength -just for this round- to (D8+2) bronze units<br>&#8226; " +
				"Repeat on the next round')");
		db.execSQL("INSERT INTO Support VALUES('Tordarroch Mastersmith', 'Blacksmith/Armourer', 2, 3, 15, '2 armies', '<br>&#8226; May Perform Weapon Forging (+1 Strenght to 8 allied melee units) once per battle<br>&#8226; " +
				"May Perform Armour Smithing (+1 Armour to 8 allied melee units) once per battle<br>&#8226; " +
				"Can only specialize in one of these abilities in each battle')");
		db.execSQL("INSERT INTO Support VALUES('Tordarroch Flaminica', 'Druid/Animal Rights Activist', 2, 3, 20, '1 army', '<br>&#8226; Can summon a Bear once per combat (Beast, 3 Hit Points, 2.5 Strength, 3 Speed, 0 Initiative, 2 Armour)<br>&#8226; " +
				"Gives +1 Strength to 3 allied units (melee or ranged) for 1 combat round <i>or</i> Restores +5 Essence to an allied @Unit Description@Druid@ or @Unit Description@Vaedermakar@ for 1 combat round<br>&#8226; " +
				"This abilities have no cooldown, and can be changed each combat round.')");
		db.execSQL("INSERT INTO Support VALUES('Priestess of Freya', 'Healer', 2, -2, 50, '1 army', '<br>&#8226; Can resurrect D6 bronze units or 1 silver unit, each turn<br>&#8226; " +
				"In non-siege battles, after the 1st combat round, this unit has a chance to get damaged in the crossfire by non-focused attacks. For each Priestess of Freya, roll a D10. If it rolls 1, it dies.<br>&#8226; " +
				"In Siege battles, the defender shall only roll D10 for the incoming non-focused ranged/siege attacks. For melee/spell/etc. attacks, roll a D20 instead. Again, if it rolls 1, a priestess dies.<br>&#8226; " +
				"After the battle is over, the player controlling the battle site can resurrect D6+2 bronze units for each priestess.')");
		db.execSQL("INSERT INTO Support VALUES('Priest of the Eternal Fire', 'Healer', 2, -2, 60, '1 army', '<br>&#8226; Can resurrect D6 bronze units every turn<br>&#8226; " +
				"In non-siege battles, after the 1st combat round, this unit has a chance to get damaged in the crossfire by non-focused attacks. For each Priest, roll a D10. If it rolls 1, it dies.<br>&#8226; " +
				"In Siege battles, the defender shall only roll D10 for the incoming non-focused ranged/siege attacks. For melee/spell/etc. attacks, roll a D20 instead. Again, if it rolls 1, a priest dies.<br>&#8226; " +
				"After the battle is over, the player controlling the battle site can resurrect (D6-1) bronze units for each priest.<br>&#8226; " +
				"Instead of healing, the Priest can waste 2 turns to light a Sacred Flame. On the start of the 3rd turn, on 5 Initiative, each Sacred Flame deals D4 Fire damage to selected groups of units, enemy or allied, cancels any Magical effects these units have, and prevents any new Magical effects of affecting them for the remainder of the turn.<br>&#8226; " +
				"Each Priest can light only one Flame per battle, and it keeps burning for the remainder of the fight, as long as the Priest is alive. Additionally, the Flames can burn out by the use of Dispelling spells, extreme weather conditions or by Fire Absorbing abilities.')");
		db.execSQL("INSERT INTO Support VALUES('Vrihedd Sapper', 'Siege Support', 2, 4, 40, '2 armies', '<br>&#8226; When fighting in enemy Fortified territory, the Sapper can undermine their defenses, dealing 1 damage to fortifications, and +1 damage for every subsequent undermining, up to a maximum of 5.<br>&#8226; " +
				"When fighting in allied Fortified territory, the Sapper can repair damaged fortifications, repairing D4 damage each turn.<br>&#8226; " +
				"When fighting in Open territories, the Sapper can attempt to build temporary defensive structures and trenches in any combat row. For each Sapper, roll a D6. For each 1-3, the sapper builds one level of their intended structure. In extreme weather conditions, each 6 will instead reduce the level of the structure by 1.<br>&#8226; " +
				"The Defensive Structure can house up to 12 non-mounted units per level, halving the damage they receive from ranged attacks and AOE spells, while enemy melee units have to waste an extra movement to attack them. It also has 3 hit points per level, and it can only be damaged by anti-building attacks. Siege attacks (like ballista’s) will hit normally. Fire damage might set it on fire (roll D6 per Fire attack, 6 lights it up) and will deal 2 damage to the building, while also dealing D4 Fire damage to the units inside.\n" +
				"The Trench can be dug in a row, reducing the Speed of 12 (per level) enemy units that try to march through this row by 1. Enemy units that end their turn in the Trench will receive +50% damage from allied Ranged and Siege attacks.')");
		// fill the gap
		db.execSQL("INSERT INTO Support VALUES('Mahakam Shield-Bearer', 'Shieldman', 2, 3, 18, '1-2 armies', '<br>&#8226; 6 Armour<br>&#8226; " +
				"Negates any Ranged anti-armour bonus damage (like from enemy Crossbowmen)<br>&#8226; " +
				"May perform Shield Ally for 4 Dwarf allies, or 2 non-dwarf ones.')");
		db.execSQL("INSERT INTO Support VALUES('Mahakam Blacksmith', 'Blacksmith/Armourer', 2, 3, 20, '1 army', '<br>&#8226; Changes the Armour of 8 units<br>&#8226; " +
				"Either with 2 Medium Armour<br>&#8226; " +
				"<i>Or</i> with 5 Heavy Armour')");
		db.execSQL("INSERT INTO Support VALUES('Mahakam Pyrotechnician', 'Dwarven Style Siege', 2, 2, 40, '1 army', '<br>&#8226; Cannot attack on the first round<br>&#8226; " +
				"Can either attack enemy fortifications, dealing 3 damage to gates per combat round.<br>&#8226; " +
				"<i>Or</i> can spend two turns moving towards the enemy siege line and attack enemy Siege, with 12 Strength (4 Dice x 3 Strength).')");
		// endregion

		//Create Navy table
		db.execSQL("CREATE TABLE Navy " +
				"( unitn TEXT PRIMARY KEY, specialization TEXT, hitpoints INTEGER, strength TEXT, speed INTEGER, initiative INTEGER, range INTEGER, cost INTEGER, armylimit TEXT, abilities TEXT, FOREIGN KEY(unitn) REFERENCES Unit(unitn))");
	}

	private void insertOverviews(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Overview VALUES('Aedirn, Lyria & Rivia', 'lyria_overview', '<br>Aedirn and Lyria are somewhat smaller kingdoms, at least in comparison with the major Northern Kingdoms. Lacking the sufficient numbers, some troops from Aedirn or Lyria underwent a great specialization to fight off specific threats. Still, the combined forces of the two nations match their neighboring ones." +
				"<br><br>Aedirn’s melee troops, the Cavaliers and the heavy-hitting Maulers, are donned in platemail and brown gambesons. Complemented by some highly-trained Longbowmen, this simple composition managed to keep the far-larger kingdom of Kaedwen at bay." +
				"<br><br>Lyria & Rivia is a more compact and generally poorer kingdom. Its main force is mainly comprised of poor inexperienced conscripts and half-skilled billmen. From this realm also hail some of the most famous soldiers in the known world. The Huszárok, or Hussars in Common, a light cavalry force which fights similarly to Kaedwen’s Dun Banner, and the Lyrian Arbalists, who use rapid-fire crossbow variations and are considered the best in the world." +
				"<br><br>The combined armies of these two nations are also followed by Rivian Warcriers, who bolster their compatriots with their trumpets. Elite troops from Aedirn, the Special Forces, armed with bows and swords, patrol the forests, searching for Scoia’tael guerillas to hone their most practiced skill: Extermination.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Kaedwen', 'kaedwen_overview', '<br>The army of Kaedwen has been majorly shaped by its eccentric climate, and its eccentric ruler. The vast majority of the soldiers are generally poor, and that is depicted on their half-decent armours and weaponry, but their discipline is rock-solid, as they are being constantly coordinated by the significant amount of high-ranking officers who fight beside them." +
				"<br><br>The thing that actually made Kaedwen a military force to be reckoned, is its outstanding cavalry, the Dun Banner. These (in)famous vsadniki, known for their characteristic Beaver hats, spread terror and disarray into enemy lines, giving enough momentum for their infantry comrades to break through." +
				"<br><br>In the back lines, protected from the onslaught, lies the Kaedweni artillery, war machines of death, that are massively produced in extreme amounts, compared to other kingdoms. The siege units are supported by capable engineers, who oversee and protect King Henselt’s “children”. Frequently acting as artillery, the mages from Ban Ard harass the opposing troops with storms, or conjure gigantic critters to aid them.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Temeria', 'temeria_overview', '<br>The Temerian army, dressed in ocean blue and silver lilies, is comprised of all social classes. Esteemed Knights in shining armours, followed by basic crossbowmen and infantry form the backbone, while drummers coordinate their march." +
				"<br><br>Aretuza’s girls, some of the best mages in the world, are seen as war heroines from Temerians, and plague the opposing forces with arcane spells, while peasants and beggars, who took up arms for their country and formed the Poor Fucking Infantry division, out-swarm the enemy troops, showing a surprising valor and determination." +
				"<br><br>But in the outskirts of Vizima, the mountainous pass of Kalmat and the forests of Velen, Temeria’s battle-hardened veterans reside. Almost invisible and invincible, they are Temeria’s silent guardians, and watchful protectors, the Blue Stripes.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Redania', 'redania_overview', '<br>Redania’s army emphasizes the use of heavily armoured melee divisions, usually comprised of the wealthy and noble families. The Redanian steeds, famous for their strength and agility, accompany the highly praised knights, equipped with lances and swords, and the Wild Ones, children of noble families who find solace battling and pillaging, into the thick of battle." +
				"<br><br>Behind them, forming the collarbone, stands the Royal Infantry, the Elites, donned in plate-mail, alongside heavy Halberdiers and various ranged divisions." +
				"<br><br>Even rock Trolls have enlisted, assaulting anything that’s not dressed in Red, while Eternal Fire’s zealots, the Witch Hunters seek to take down enemy spellcasters.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Nilfgaardian Empire', 'nilfgaardian_empire_overview', '<br>For an empire to expand to the corners of the continent, it must possess the proper military to do so. With superior discipline and equipment, and the best logistics and training, it is no wonder that Nilfgaard is already halfway there." +
				"<br><br>Heavily armoured with top-quality platemail, the Alba and Daerlan soldiers form the army’s meat, while the Impera Brigade serve as bodyguards to essential personas. Protected behind the infantry lines, the Black Infantry Archers, and the Impera Enforcers -with their shields on their backs- rain death upon the enemy. The Venendal cavalry from Ebbing supports the army with quick assaults, but the true mounted terror, a galloping Panzer wielding Warhammers, is called Nausicaa." +
				"<br><br>Sent to Korath desert, to protect the imperial borders, the Mange division, a fast infantry unit, specializes in heat and drought. One step below exist the slaves, from the conquered provinces, who serve as mediocre arrow fodder for the empire’s cause." +
				"<br><br>All these troops are accompanied by a plethora of scientists and craftsmen of all sorts. From the Vicovarian Academy come the Novices, young mages who prove their mettle through warfare, with the bulky Imperial Golems at their side. But the true stars of Vicovaro are the Medics, with their bird masks and sharpened scalpels. Alchemists from the capital mix compounds to create potions, acids and flammable mixtures to support the war effort." +
				"<br><br>This professional troupe is complemented by a vast number of siege engines and Engineers, ready to repair any malfunction. The key to bringing cities to their knees, Nilfgaard’s unethical specialty, the Rot Tosser, which flings rotten carcasses over the enemy walls, allowing the Catriona plague to spread. Finally, from Zerrikania in the far-east, comes the ultimate war machine, that shoots exploding firebolts, that engulf whole battalions in their greenish flames.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Skellige & Cintra', 'skellige_overview', '<br>Cintra, a wealthy and strong coastal kingdom, bordering the intimidating Nilfgaardian Empire to the south, reached its peak at the time of Queen Calanthe, the lioness of Cintra. At her time, the kingdom had sufficient resources to field an army of Heavy and Light infantry, Crossbowmen and Knights, to protect itself. But that was not the accomplishment that made her famous." +
				"<br><br>Instead, it was the mating of the golden lion with the white bear, in the azure fields and oceans, the strategic marriage with King Eist of the Skellige Isles, that cemented Cintra’s position as both a land and marine powerhouse." +
				"<br><br>The six islands of Skellige are ruled by seven local Jarls with a general independence, but act as one fist at their King’s call." +
				"<br><br>This clan-based culture creates an astonishing variety of troop combinations and military techniques. Quick pillagers, with round shields and straight blades, accompanied by half-naked men with claymores, heavily armoured Axe-men and dark-clothed Poachers, all in the same battlefield. Sea-faring corsairs, who excel at hijacking enemy vessels, and ballsy girls, armed with just a spear, who fight polar bears for breakfast. Even musicians, inexperienced in combat, rush into the fray, with their lutes and their insanity to guide them." +
				"<br><br>All this frenzied paranoia is accompanied by a more calm, collected, and dark side of the Isles. Druid Circles are fairly common there, with Flaminicas and Vaedermakars showing an intellectual approach with their arcane skills. Worshippers of Freya are also very common, and many priestesses have extraordinary healing powers. Rumors even suggest that worshippers of Svalblod also dwell on the Isles, and legends speak of their grotesque shape-shifting abilities…<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Kovir & Poviss', 'kovir_overview', '<br>The kingdom of Kovir & Poviss is mostly well known for its ability to evade wars, rather than partake. Its “eternal neutrality” has allowed the economy, society and science of the nation to evolve, which brought many military innovations to the Koviri army, among other things. In the rare instances of war, the highly equipped and well-trained soldiers of the kingdom in the far north have proven their mettle, being able to outclass the armies of much larger kingdoms or empires." +
				"<br><br>As with any sophisticated army composition, the Koviri Infantrymen, namely the Picchierres and the Condottieri, serve as the backbone, the latter of which are also sent to fight for other kingdoms as mercenaries, for the right price. Even though the soldiers are of the highest quality, their numbers are limited due to the kingdom’s low population, and for this reason, an elite fighting unit was financed by Kovir, Adieu’s Free Company, consisted of war veterans and savage convicts and murderers, who were released on condition to die for their homeland on the field." +
				"<br><br>The meat of the army is supported by Crossbowmen and a limited amount of Paladins, heavily armoured cavalrymen, and mounted Arquebusiers, hard-hitting mobile shooters that use a peculiar device that launches small metallic lumps with the power of gunpowder." +
				"<br><br>Kovir’s loud embrace towards magic and science has brought many highly capable scholars and magisters, who can spell doom for enemy nations, in the form of either powerful spells, or innovative siege engines, like the Carambola, a large and heavy tube that fires humongous cannonballs, which specializes in obliterating fortifications or enemy siege units before they can even attempt to fire back.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Hengfors League', 'hengfors_league_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Novigrad', 'novigrad_overview', '<br>The free city of Novigrad, a grandiose metropolis surrounded by Redanian lands, capital of culture and commerce, and the seat of the church of the Eternal Fire, has retained its immense and ever-growing wealth and independence through a combination of diplomacy, naval might and religious fanatism." +
				"<br><br>The City is governed by the Hierarch himself, head of the church, while being protected by the Novigrad Security Bureau, whose armed hand, the Temple Guard, patrols the streets vigilantly. Other religious militant groups such as the Witch Hunters and the Order of the Flaming Rose serve the church and protect its interests." +
				"<br><br>Novigrad is also notorious for its organized crime network, consisted of several gangs that control the various means of illegal business, namely gambling, smuggling, thievery and assassination contracts. Given the wealth and influence these crime lords accumulate, they possess a strong hand in the city’s political game.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Scoia’tael', 'scoiatael_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Brokilon', 'brokilon_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Mahakam', 'mahakam_overview', '<br>The sturdy, iron-willed Dwarves of Mahakam protect their homeland with great care, ensuring that nothing can pass through the paths of Mount Gorge without their permission." +
				"<br><br>They have formed the Mahakam Volunteer Army, to assist their Liege, the King of Temeria, in times of War, and eagerly jump into the thickest of battle, to crush their enemies under their boots." +
				"<br><br>Their ranks are consisted by Heavy, slow-moving infantry, with dense, mastercrafted platemail and spears or axes by their side, and experienced Crossbowmen, using fast-firing Arbalests, who hide between the lines of Shield Bearers, whose task is to keep their shields up and their allies safe." +
				"<br><br>Further back, a gathering of experienced Craftsmen build and fix Armours of the finest quality, and Alchemists create explosive substances, before rushing between the enemy lines to set their war machines on fire.<br>') ");
		db.execSQL("INSERT INTO Overview VALUES('Brugge', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Sodden', 'sodden_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Cidaris', 'cidaris_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Kerack', 'kerack_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Verden', 'verden_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Angren', 'angren_oveview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Toussaint', 'toussaint_overview', '<i><br>Born from the molten ashes of the surrounding mountain range<br>" +
				"The valley on which Toussaint has grown<br>" +
				"As if a fallen phoenix in its fiery grave<br>" +
				"The seeds of fairytales has sown" +
				"<br><br>" +
				"In sun-doused dawns and starry nights<br>" +
				"The Duchy stays tipsy on wine<br>" +
				"And in the vineyards of Dun Tynne<br>" +
				"Fair maidens find their gallant knights" +
				"<br><br>" +
				"But deep down in the hollow mines<br>" +
				"Trapped in this realm since ancient times<br>" +
				"Eternal unseen beings reside<br>" +
				"Hence fables and deceits collide<br></i>') ");
		db.execSQL("INSERT INTO Overview VALUES('Nazair', 'nazair_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Mag Turga', 'mag_turga_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Ebbing', 'ebbing_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Metinna', 'metinna_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Geso', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Maecht', 'maecht_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Gemmera', 'gemmera_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Rowan', 'rowan_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Ymlac', 'ymlac_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Etolia', 'etolia_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Vicovaro', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Ofier', 'ofier_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Zerrikania', 'zerrikania_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Haakland', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Wild Hunt', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Strays of Spalla', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Salamandra', 'ic_overview', '') ");
		db.execSQL("INSERT INTO Overview VALUES('Neutral', 'ic_overview', '') ");
	}

	private void insertTerritories(SQLiteDatabase db) {
		// region Fortified Territories
		db.execSQL("INSERT INTO Territory VALUES(1, 'Tridam', 'Kovir & Poviss', 'Fortified', 'Poviss', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(2, 'Lan Exeter', 'Kovir & Poviss', 'Fortified', 'Kovir', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(3, 'Pont Vanis', 'Kovir & Poviss', 'Fortified', 'Kovir', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(4, 'Rakverelin', 'Kovir & Poviss', 'Fortified', 'Narok', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(5, 'Tancarville', 'Hengfors', 'Fortified', 'Creyden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(6, 'Creigiau', 'Hengfors', 'Fortified', 'Malleore', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(7, 'Hengfors', 'Hengfors', 'Fortified', 'Caingorn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(8, 'Barefield', 'Hengfors', 'Fortified', 'Barefield', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(9, 'Yamurlak', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(10, 'Gelibol', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(11, 'Barlenmurg', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(12, 'Denesle', 'Redania', 'Fortified', 'Redania', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(13, 'Novigrad', 'Novigrad', 'Fortified', 'Redania', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(14, 'Oxenfurt', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(15, 'Tretogor', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(16, 'Drakenborg', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(17, 'Montecalvo', 'Redania', 'Fortified', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(18, 'Vartburg', 'Redania', 'Fortified', 'Redania', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(19, 'Ban Glean', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(20, 'Shaerrawedd', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(21, 'Ard Carraigh', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(22, 'Vattweir', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(23, 'Daevon', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(24, 'Vspaden', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(25, 'Kaer Morhen', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(26, 'Ban Ard', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(27, 'Leyda', 'Kaedwen', 'Fortified', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(28, 'Loc Muine', 'Kaedwen', 'Fortified', 'Blue Mt.', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(29, 'Vergen', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(30, 'Hagge', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(31, 'Gulet', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(32, 'Hoshberg', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(33, 'Vengerberg', 'Aedirn', 'Fortified', 'Aedirn', 0, '<br>@Unit Description@Imperial Golem@') ");
		db.execSQL("INSERT INTO Territory VALUES(34, 'Asheberg', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(35, 'Eysenlaan', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(36, 'Aldersberg', 'Aedirn', 'Fortified', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(37, 'Lyria', 'Lyria & Rivia', 'Fortified', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(38, 'Spalla', 'Lyria & Rivia', 'Fortified', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(39, 'Scala', 'Lyria & Rivia', 'Fortified', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(40, 'Rivia', 'Lyria & Rivia', 'Fortified', 'Lyria', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(41, 'Mount Carbon', 'Mahakam', 'Fortified', 'Mahakam', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(42, 'Kagen', 'Sodden', 'Fortified', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(43, 'Carcano', 'Sodden', 'Fortified', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(44, 'Armeria', 'Sodden', 'Fortified', 'Sodden', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(45, 'Vidort', 'Brugge', 'Fortified', 'Brugge', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(46, 'Dillingen', 'Brugge', 'Fortified', 'Brugge', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(47, 'Brugge', 'Brugge', 'Fortified', 'Brugge', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(48, 'Mayena', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(49, 'Razawan', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(50, 'Maribor', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(51, 'Carreras', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(52, 'Ellander', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(53, 'La Valette', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(54, 'Vizima', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(55, 'Dorian', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(56, 'Gors Velen', 'Temeria', 'Fortified', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(57, 'Thanedd Isle', 'Temeria', 'Fortified', 'Temeria', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(58, 'Rissberg', 'Cidaris', 'Fortified', 'Cidaris', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(59, 'Cidaris', 'Cidaris', 'Fortified', 'Cidaris', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(60, 'Petrelsteyn', 'Kerack', 'Fortified', 'Kerack', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(61, 'Kerack', 'Kerack', 'Fortified', 'Kerack', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(62, 'Bodrog', 'Verden', 'Fortified', 'Verden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(63, 'Rozrog', 'Verden', 'Fortified', 'Verden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(64, 'Nastrog', 'Verden', 'Fortified', 'Verden', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(65, 'Kaer Trolde', 'Skellige', 'Fortified', 'Ard Skellig', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(66, 'Kaer Muire', 'Skellige', 'Fortified', 'Ard Skellige', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(67, 'Cintra', 'Cintra', 'Fortified', 'Cintra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(68, 'Attre', 'Cintra', 'Fortified', 'Cintra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(69, 'Ortagor', 'Cintra', 'Fortified', 'Cintra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(70, 'Tigg', 'Cintra', 'Fortified', 'Cintra', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(71, 'Rhyus-Rhun', 'Nilfgaardian Empire, Nazair', 'Fortified', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(72, 'Castel Ravello', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(73, 'Corvo', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(74, 'Tesham Mutna', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(75, 'Beauclair', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(76, 'Pomerol', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(77, 'Bellhaven', 'Nilfgaardian Empire, Toussaint', 'Fortified', 'Toussaint', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(78, 'Riedbrune', 'Angren', 'Fortified', 'Dol Angra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(79, 'Glevitzingen', 'Angren', 'Fortified', 'Dol Angra', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(80, 'Gwendeith', 'Scoia’tael', 'Fortified', 'Blue Mts.', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(81, 'Tergano', 'Nilfgaardian Empire, Mag Turga', 'Fortified', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(82, 'Neunreuth', 'Nilfgaardian Empire, Metinna', 'Fortified', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(83, 'Forgeham', 'Nilfgaardian Empire, Metinna', 'Fortified', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(84, 'Metinna', 'Nilfgaardian Empire, Metinna', 'Fortified', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(85, 'Fen Aspra', 'Nilfgaardian Empire, Geso', 'Fortified', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(86, 'Amarillo', 'Nilfgaardian Empire, Geso', 'Fortified', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(87, 'Tyffi', 'Nilfgaardian Empire, Geso', 'Fortified', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(88, 'Druigh', 'Nilfgaardian Empire, Geso', 'Fortified', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(89, 'Sarda', 'Nilfgaardian Empire, Geso', 'Fortified', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(90, 'Neweugen', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(91, 'Tegamo', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(92, 'Claremont', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(93, 'Fano', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(94, 'Venendal', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(95, 'Dun Dâre', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(96, 'Salm', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(97, 'Stygga', 'Nilfgaardian Empire, Ebbing', 'Fortified', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(98, 'Maecht', 'Nilfgaardian Empire, Maecht', 'Fortified', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(99, 'Rocayne', 'Nilfgaardian Empire, Maecht', 'Fortified', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(100, 'Thurn', 'Nilfgaardian Empire, Maecht', 'Fortified', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(101, 'Pacifiers’ Fort', 'Nilfgaardian Empire, Gemmera', 'Fortified', 'Gemmera', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(102, 'Darn Rowan', 'Nilfgaardian Empire, Rowan', 'Fortified', 'Rowan', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(103, 'Academy of Magic', 'Nilfgaardian Empire, Etolia', 'Fortified', 'Etolia', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(104, 'Eiddon', 'Nilfgaardian Empire, Etolia', 'Fortified', 'Etolia', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(105, 'Darn Dyffra', 'Nilfgaardian Empire, Vicovaro', 'Fortified', 'Vicovaro', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(106, 'Vicovaro Academy', 'Nilfgaardian Empire, Vicovaro', 'Fortified', 'Vicovaro', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(107, 'Baccalà', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(108, 'Xarthisius’ Tower', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(109, 'Loc Grim Castle', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(110, 'Viroleda', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(111, 'Ruach', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(112, 'Darn Rach', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(113, 'Tarnhann', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(114, 'Liddertal Castle', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(115, 'Winneburg', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(116, 'Castel Graupian', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(117, 'Winneburg Castle', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(118, 'Nilfgaard', 'Nilfgaardian Empire', 'Fortified', 'Nilfgaard', 0, '') ");
		// endregion

		// region Open Territories
		db.execSQL("INSERT INTO Territory VALUES(1, 'Tonia Valley', 'Kovir & Poviss', 'Open', 'Poviss', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(2, 'Velhad', 'Kovir & Poviss', 'Open', 'Poviss', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(3, 'Talgar', 'Kovir & Poviss', 'Open', 'Poviss', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(4, 'Kestrel Foothills', 'Hengfors', 'Open', 'Caingorn', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(5, 'Tango Delta', 'Kovir & Poviss', 'Open', 'Kovir', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(6, 'Yspaden', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(7, 'Luton', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(8, 'Blaviken', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(9, 'Lutonski Road', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(10, 'Mirt', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(11, 'Crinfrid', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(12, 'Roggeven', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(13, 'Gustfields', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(14, 'Rinde', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(15, 'Foam', 'Redania', 'Open', 'Redania', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(16, 'Murivel', 'Redania', 'Open', 'Redania', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(17, 'Egremont Glen', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(18, 'Beeches', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(19, 'Kaedwen Trail', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(20, 'Lutonski Road II', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(21, 'Buina Fields', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(22, 'Vspaden Valley', 'Kaedwen', 'Open', 'Vspaden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(23, 'The Killer', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(24, 'Kaer Morhen Valley', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(25, 'Hertch', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(26, 'Lixeda Valley', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(27, 'Pontar Clearing', 'Kaedwen', 'Open', 'Kaedwen', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(28, 'Est Haemlet', 'Scoia’tael', 'Open', 'Blue Mts.', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(29, 'Berg Aen Dal', 'Aedirn', 'Open', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(30, 'Tiel', 'Aedirn', 'Open', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(31, 'Pontar Valley', 'Aedirn', 'Open', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(32, 'Kalkar', 'Aedirn', 'Open', 'Aedirn', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(33, 'Dyfne Fork', 'Aedirn', 'Open', 'Aedirn', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(34, 'Feainnewedd Blossoms', 'Scoia’tael', 'Open', 'Dol Blathanna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(35, 'Upper Posada', 'Scoia’tael', 'Open', 'Dol Blathanna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(36, 'Lower Posada', 'Scoia’tael', 'Open', 'Dol Blathanna', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(37, 'Lyria Outskirts', 'Lyria & Rivia', 'Open', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(38, 'Dillmoor', 'Lyria & Rivia', 'Open', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(39, 'Rastburg', 'Lyria & Rivia', 'Open', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(40, 'Loc Eskalott', 'Lyria & Rivia', 'Open', 'Lyria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(41, 'Red Port', 'Lyria & Rivia', 'Open', 'Angren', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(42, 'Slopes', 'Sodden', 'Open', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(43, 'Rialto Mine', 'Sodden', 'Open', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(44, 'Loc Munduirn', 'Sodden', 'Open', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(45, 'Sodden Hill', 'Sodden', 'Open', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(46, 'Caed Dhu', 'Sodden', 'Open', 'Sodden', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(47, 'Dregsdon', 'Sodden', 'Open', 'Sodden', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(48, 'Turlough Hills', 'Brugge', 'Open', 'Brugge', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(49, 'Owl Hills', 'Brugge', 'Open', 'Brugge', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(50, 'Burdoff', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(51, 'Zavada', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(52, 'Bondar', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(53, 'Flotsam', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(54, 'Brenna', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(55, 'White Orchard', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(56, 'Hirundum', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(57, 'Windley', 'Temeria', 'Open', 'Temeria', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(58, 'Crow’s Perch', 'Temeria', 'Open', 'Temeria', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(59, 'Vole', 'Cidaris', 'Open', 'Cidaris', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(60, 'Bremervood', 'Cidaris', 'Open', 'Cidaris', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(61, 'Craag An', 'Brokilon', 'Open', 'Brokilon', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(62, 'Duén Canell', 'Brokilon', 'Open', 'Brokilon', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(63, 'Cean Treise', 'Brokilon', 'Open', 'Brokilon', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(64, 'Col Serrai', 'Brokilon', 'Open', 'Brokilon', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(65, 'Ansegis', 'Kerack', 'Open', 'Kerack', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(66, 'Hamm', 'Verden', 'Open', 'Verden', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(67, 'Urialla Harbour', 'Skellige', 'Open', 'An Skellig', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(68, 'Svorlag', 'Skellige', 'Open', 'Spikeroog', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(69, 'Larvik', 'Skellige', 'Open', 'Hindarsfjall', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(70, 'Harviken', 'Skellige', 'Open', 'Faroe', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(71, 'Tordarroch Forge', 'Skellige', 'Open', 'Undvik', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(72, 'Erlenwald', 'Cintra', 'Open', 'Cintra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(73, 'Hochebuz', 'Cintra', 'Open', 'Cintra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(74, 'Marnadal Stairs', 'Cintra', 'Open', 'Cintra', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(75, 'Amiel Foothills', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(76, 'Muredach', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(77, 'Great-Sea Ports', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(78, 'Yelena Valley', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(79, 'Nazair Plains', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(80, 'Assengard', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(81, 'Theodula Pass', 'Nilfgaardian Empire, Nazair', 'Open', 'Nazair', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(82, 'Fox Hollow', 'Nilfgaardian Empire, Toussaint', 'Open', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(83, 'Dun Tyne', 'Nilfgaardian Empire, Toussaint', 'Open', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(84, 'Caer Myrkvid', 'Nilfgaardian Empire, Toussaint', 'Open', 'Toussaint', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(85, 'Cervantes Pass', 'Nilfgaardian Empire, Toussaint', 'Open', 'Toussaint', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(86, 'Ysgith', 'Angren', 'Open', 'Dol Angra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(87, 'Kluzc', 'Angren', 'Open', 'Dol Angra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(88, 'Kazcan', 'Angren', 'Open', 'Dol Angra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(89, 'Porog', 'Angren', 'Open', 'Dol Angra', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(90, 'Dire Mire', 'Angren', 'Open', 'Dol Angra', 0, '') ");

		db.execSQL("INSERT INTO Territory VALUES(91, 'Sudduth Valley', 'Nilfgaardian Empire, Mag Turga', 'Open', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(92, 'Caravista', 'Nilfgaardian Empire, Mag Turga', 'Open', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(93, 'Solveiga Pass', 'Nilfgaardian Empire, Mag Turga', 'Open', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(94, 'Mallheur Pass', 'Nilfgaardian Empire, Mag Turga', 'Open', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(95, 'Mortblanc Pass', 'Nilfgaardian Empire, Mag Turga', 'Open', 'Mag Turga', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(96, 'Sylte Falls', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(97, 'Caenlloch', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(98, 'Mil Trachta', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(99, 'Tarn Mira', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(100, 'Tor Zirael', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(101, 'Oceanview', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(102, 'Sylte Delta', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(103, 'Fertile Plains', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(104, 'Forgeham Glade', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(105, 'Mag Deira', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(106, 'Metinna Outskirts', 'Nilfgaardian Empire, Metinna', 'Open', 'Metinna', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(107, 'New Forge', 'Nilfgaardian Empire, Geso', 'Open', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(108, 'Glyswen', 'Nilfgaardian Empire, Geso', 'Open', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(109, 'Loredo', 'Nilfgaardian Empire, Geso', 'Open', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(110, 'White River', 'Nilfgaardian Empire, Geso', 'Open', 'Geso', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(111, 'Birka', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(112, 'Jealousy', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(113, 'Venendal Coast', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(114, 'Swamplands', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(115, 'Unicorn', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(116, 'Pereplut Bog', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(117, 'Malhoun', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(118, 'Salm Riverview', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(119, 'Tonnerre', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(120, 'Im Lebar Fork', 'Nilfgaardian Empire, Ebbing', 'Open', 'Ebbing', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(121, 'Flood Plains', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(122, 'Velda Junction', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(123, 'Elskerdeg Pass', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(124, 'Tir Tochair Plateau', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(125, 'Dadnu', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(126, 'Alluvial Plains', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(127, 'Gnome Hillside', 'Nilfgaardian Empire, Maecht', 'Open', 'Maecht', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(128, 'Tochair Foothills', 'Nilfgaardian Empire, Gemmera', 'Open', 'Gemmera', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(129, 'Gemmera Glades', 'Nilfgaardian Empire, Gemmera', 'Open', 'Gemmera', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(130, 'Ymlac', 'Nilfgaardian Empire, Ymlac', 'Open', 'Ymlac', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(131, 'Madrake Fields', 'Nilfgaardian Empire, Ymlac', 'Open', 'Ymlac', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(132, 'Alba Fork', 'Nilfgaardian Empire, Rowan', 'Open', 'Rowan', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(133, 'Rowan', 'Nilfgaardian Empire, Rowan', 'Open', 'Rowan', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(134, 'Porto Katsiki', 'Nilfgaardian Empire, Etolia', 'Open', 'Etolia', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(135, 'Vicovaro Leas', 'Nilfgaardian Empire, Vicovaro', 'Open', 'Vicovaro', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(136, 'The Riversection', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(137, 'Loc Grim', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(138, 'Etolian Ports', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(139, 'Grim Swamps', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(140, 'Viroleda Outskirts', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(141, 'Ruach Plains', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(142, 'Tarnhann Glade', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(143, 'Liddertal', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(144, 'Golden Fields', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		db.execSQL("INSERT INTO Territory VALUES(145, 'Wiineburg', 'Nilfgaardian Empire', 'Open', 'Nilfgaard', 0, '') ");
		// endregion

		//Create Fortified table
		db.execSQL("CREATE TABLE Fortified " +
				"( territoryn TEXT PRIMARY KEY, status TEXT, defences TEXT, FOREIGN KEY(territoryn) REFERENCES Territory(territoryn))");

		// region Insert Fortified entries
		db.execSQL("INSERT INTO Fortified VALUES('Tridam', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Lan Exeter', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Pont Vanis', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Rakverelin', 'Town', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Tancarville', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Creigiau', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Hengfors', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Barefield', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Yamurlak', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Gelibol', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Barlenmurg', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Denesle', 'Fort', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Novigrad', 'Capital', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Oxenfurt', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tretogor', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Drakenborg', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Montecalvo', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vartburg', 'Fort', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Ban Glean', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Shaerrawedd', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Ard Carraigh', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vattweir', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Daevon', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vspaden', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Kaer Morhen', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Ban Ard', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Leyda', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Loc Muine', 'Town', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Vergen', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Hagge', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Gulet', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Hoshberg', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vengerberg', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Asheberg', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Eysenlaan', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Aldersberg', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Lyria', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Spalla', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Scala', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Rivia', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Mount Carbon', 'Capital', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Kagen', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Carcano', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Armeria', 'Fort', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Vidort', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Dillingen', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Brugge', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Mayena', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Razawan', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Maribor', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Carreras', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Ellander', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('La Valette', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vizima', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Dorian', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Gors Velen', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Thanedd Isle', 'Castle', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Rissberg', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Cidaris', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Petrelsteyn', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Kerack', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Bodrog', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Rozrog', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Nastrog', 'City', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Kaer Trolde', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Kaer Muire', 'Castle', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Cintra', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Attre', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Ortagor', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tigg', 'Fort', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Rhyus-Rhun', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Castel Ravello', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Corvo', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tesham Mutna', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Beauclair', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Pomerol', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Bellhaven', 'Town', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Riedbrune', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Glevitzingen', 'Fort', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Gwendeith', 'Castle', '') ");

		db.execSQL("INSERT INTO Fortified VALUES('Tergano', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Neunreuth', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Forgeham', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Metinna', 'Capital', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Fen Aspra', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Amarillo', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tyffi', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Druigh', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Sarda', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Neweugen', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tegamo', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Claremont', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Fano', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Venendal', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Dun Dâre', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Salm', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Stygga', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Maecht', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Rocayne', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Thurn', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Pacifiers’ Fort', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Darn Rowan', 'Fort', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Academy of Magic', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Eiddon', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Darn Dyffra', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Vicovaro Academy', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Baccalà', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Xarthisius’ Tower', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Loc Grim Castle', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Viroleda', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Ruach', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Darn Rach', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Tarnhann', 'Town', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Liddertal Castle', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Winneburg', 'City', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Castel Graupian', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Winneburg Castle', 'Castle', '') ");
		db.execSQL("INSERT INTO Fortified VALUES('Nilfgaard', 'Capital', '') ");
		// endregion
	}

	private void insertBestiaries(SQLiteDatabase db) {
		db.execSQL("INSERT INTO Bestiary VALUES('Beasts', 'ic_menu_monsters') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Cursed', 'ic_cursed') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Draconids', 'ic_draconids') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Elementae & Constructs', 'ic_elementae_constructs') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Hybrids', 'ic_hybrids') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Insectoids', 'ic_menu_monsters') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Necrophages', 'ic_necrophages') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Ogroids', 'ic_ogroids') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Relicts', 'ic_menu_monsters') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Specters', 'ic_specters') ");
		db.execSQL("INSERT INTO Bestiary VALUES('Vampires', 'ic_vampires') ");
	}

	private void insertMonsters(SQLiteDatabase db) {
		// region insert Beasts
		db.execSQL("INSERT INTO Monster VALUES('Wolf', 'Beasts', 'Melee Beast', 1, '1', 3, 0, 0, '<br>&#8226; Pack Tactics - If more than 10 allied wolves are present on the battlefield, they all gain +0.5 Strength.', 1, 'wolf')");
		db.execSQL("INSERT INTO Monster VALUES('Bear', 'Beasts', 'Melee Beast', 3, '2.5', 3, 0, 0, '<br>&#8226; 2 Armour<br>&#8226; " +
				"Immune to Ice and Wind damage', 2, 'bear')");
		// endregion

		// region insert Cursed
		db.execSQL("INSERT INTO Monster VALUES('Archespore', 'Cursed', 'Ranged Cursed', 6, '8 (2 Dice x 4 Strength)', 2, 2, 2, '<br>&#8226; Deals Poison damage<br>&#8226; " +
				"Receives Double damage from Fire and Wind attacks<br>&#8226; " +
				"Upon dying, deals (D4 - 1) damage to each tile around it', 1, 'archespore')");
		db.execSQL("INSERT INTO Monster VALUES('Werewolf', 'Cursed', 'Melee Cursed', 20, 'D6+4', 4, 2, 0, '<br>&#8226; Regenerates 10 Hit Points each combat round<br>&#8226; " +
				"Receives double damage from Poison attacks<br>&#8226; " +
				"Regeneration is halted when Poisoned, or under the effect of Moon Dust<br>&#8226; " +
				"Summon Wolf Pack: Summons a (D8+2) number of wolves each turn. These wolves will always protect their Conjurer, and must be dealt with before he can be hurt.', 2, 'werewolf')");
		db.execSQL("INSERT INTO Monster VALUES('Ulfhedinn', 'Cursed', 'Melee Cursed', 30, 'D8+7', 5, 2, 0, '<br>&#8226; Regenerates 20 Hit Points each combat round<br>&#8226; " +
				"Receives double damage from Poison attacks<br>&#8226; " +
				"Regeneration is halved (10 Hit Points each combat round) when Poisoned, or under the effect of Moon Dust<br>&#8226; " +
				"Summon Wolf Pack: Summons a (D8+2) number of wolves each turn. These wolves will always protect their Conjurer, and must be dealt with before he can be hurt.', 3, 'ulfhedinn')");
		// endregion

		// region insert Draconids
		db.execSQL("INSERT INTO Monster VALUES('Wyvern', 'Draconids', 'Melee Draconid', 40, 'D6+3', 5, 0, 0, '<br>&#8226; Deals Poison damage.<br>&#8226; " +
				"Can fly, but still need to be in melee to attack.<br>&#8226; " +
				"May perform Hit and Run.<br>&#8226; " +
				"Receives only half damage from Fire attacks.', 1, 'wyvern')");
		db.execSQL("INSERT INTO Monster VALUES('Cockatrice', 'Draconids', 'Melee Draconid', 30, '2D8', 6, 1, 0, '<br>&#8226; Can fly, but still need to be in melee to attack. However, its positive initiative allows to stay safe from most melee retaliations.<br>&#8226; " +
				"May perform Hit and Run.<br>&#8226; " +
				"Receives double damage from Piercing attacks (arrows, spears, etc.)', 2, 'cockatrice')");
		db.execSQL("INSERT INTO Monster VALUES('Forktail', 'Draconids', 'Melee Draconid', 35, '2D6+1', 5, 0, 0, '<br>&#8226; Deals Poison damage.<br>&#8226; " +
				"Can fly, but still need to be in melee to attack.<br>&#8226; " +
				"May perform Hit and Run.', 3, 'forktail')");
		db.execSQL("INSERT INTO Monster VALUES('Slyzard', 'Draconids', 'Melee/Ranged Draconid', 40, 'D6+3', 5, 0, 0, '<br>&#8226; Can attack with a Ranged attack, with 1 range and initiative. This attack deals D8 Fire damage.<br>&#8226; " +
				"Immune to Fire damage.<br>&#8226; " +
				"Can fly.<br>&#8226; " +
				"May perform Hit and Run.', 4, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Phoenix', 'Draconids', 'Melee Draconid', 40, '2D10', 5, 1, 0, '<br>&#8226; Deals Fire damage.<br>&#8226; " +
				"Immune to Fire damage.<br>&#8226; " +
				"Can fly, but still need to be in melee to attack. However, its positive initiative allows to stay safe from most melee retaliations.<br>&#8226; " +
				"May perform Hit and Run.<br>&#8226; " +
				"Receives half damage from Ranged and Magic attacks.<br>&#8226; " +
				"When killed by a melee attack, the Phoenix deals 3D10 Fire damage to its attacker. It then arises again with 40-3D10 Hit Points (the same number used in the aforementioned attack). If killed by a non-melee attack, the Phoenix rebirths as usual, and its first attack deals an additional 3D10 Fire damage. Once per battle.', 5, 'phoenix')");
		db.execSQL("INSERT INTO Monster VALUES('Chelondrake', 'Draconids', 'Melee Draconid', 70, '', 5, 0, 0, '<br>&#8226; May attack ships<br>&#8226; " +
				"Immune to all damage from non-magical, non-siege or non-ship attacks.', 6, 'chelondrake')");
		// endregion

		// region insert Elementae & Constructs
		db.execSQL("INSERT INTO Monster VALUES('Fire Elemental', 'Elementae & Constructs', 'Ranged Elemental', 20, '18 (6 Dice x 3 Strength)', 2, 2, 4, '<br>&#8226; Attacks deal Fire damage<br>&#8226; " +
				"Wind attacks against it increase its Strength by 6 (2 x 3), but deal normal damage to it.<br>&#8226; " +
				"Draws in any Fire type attacks and abilities to raise its Strength by 6 (2 x 3) each time.<br>&#8226; " +
				"Receives double damage from Water and Earth attacks.<br>&#8226; " +
				"If hit by Ice attacks, transmutes them into Water, who in turn deal double damage.<br>&#8226; " +
				"Successful hits Ignite adjacent enemy units, attempting to engulf them in their flames with 2 Strength (2 Dice x 1 Strength).', 1, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Water Elemental', 'Elementae & Constructs', 'Ranged Elemental', 25, '12 (3 Dice x 4 Strength)', 2, 2, 6, '<br>&#8226; Attacks deal Water Damage<br>&#8226; " +
				"Under the presence of significant effects of its Element (like floods, storms, etc.) its Strength is increased by 4.<br>&#8226; " +
				"Liquid Armour – all non-magic attacks and Fire attacks deal half damage. Lightning attacks deal double damage.<br>&#8226; " +
				"Wind attacks lower its initiative by 1 for 1 turn.', 2, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Earth Elemental', 'Elementae & Constructs', 'Armoured Melee Elemental', 25, '10 (2 Dice x 5 Strength)', 2, 0, 0, '<br>&#8226; 30 Armour<br>&#8226; " +
				"Attacks deal Earth damage.<br>&#8226; " +
				"Receives half damage from all magical and ranged attacks.<br>&#8226; " +
				"Completely immune to Lightning attacks.<br>&#8226; " +
				"Water attacks deal normal damage and ignore its Armour.<br>&#8226; " +
				"Wind attacks deal double damage and reduce its movement and initiative by 2, for 1 turn.', 3, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Wind Elemental', 'Elementae & Constructs', 'Mobile Ranged Elemental', 15, '12 (6 Dice x 2 Strength)', 6, 4, 2, '<br>&#8226; May perform Hit and Run.<br>&#8226; " +
				"Attacks deal Wind damage.<br>&#8226; " +
				"Receives half damage from Water attacks.<br>&#8226; " +
				"Receives double damage from Ice attacks and movement is reduced by 2 for 1 turn.<br>&#8226; " +
				"Lightning attacks reduce its Movement and Initiative by 2 for 2 turns (but deal normal damage, effect may stack).', 4, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Lightning Elemental', 'Elementae & Constructs', 'Ranged Elemental/Caster', 20, '15 (5 Dice x 3 Strength)', 3, 2, 4, '<br>&#8226; Attacks deal Lightning damage.<br>&#8226; " +
				"May cast Lightning Bolt, dealing (D8+2) damage to a gathering of units. Once every 4 Combat Rounds.<br>&#8226; " +
				"Draws in all Lightning attacks and abilities to increase its Strength by 3, and Ligthning Bolt’s damage by 2, each time.<br>&#8226; " +
				"Receives double damage from Earth Attacks and from Trebuchet and Onager attacks (and their variants).', 5, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Ice Elemental', 'Elementae & Constructs', 'Ranged Elemental (?) TODO', 0, '', 0, 0, 0, '<br>&#8226; TODO', 6, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Golem', 'Elementae & Constructs', 'Melee Armoured Construct', 20, '12 (4 Dice x 3 Strength)', 2, 0, 0, '<br>&#8226; 20 Armour<br>&#8226; " +
				"Immune to Magic -that includes allied and enemy spells, and mages’ ranged attacks-<br>&#8226; " +
				"Wind attacks and abilities may stagger it for 1 turn (roll a D6, if it rolls a 5-6, it’s staggered). They will still deal no damage.<br>&#8226; " +
				"May perform Stone Cage, that isolates itself and up to 10 other units adjacent to it -ally or enemy-, and makes all 11 of them invulnerable to attacks from outside. Lasts for 2 turns.', 7, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Gargoyle', 'Elementae & Constructs', 'Melee & Ranged Armoured Construct', 20, '\nMelee Strength: 15 (5 Dice x 3 Strength)\nRanged Strength: 9 (3 Dice x 3 Strength)', 2, -1, 4, '<br>&#8226; May perform Ranged attack with 2 Initiative instead of attacking in Melee.<br>&#8226; " +
				"15 Armour<br>&#8226; " +
				"May attack Siege Engines with its Ranged Attack with 4 Strength (2 Dice x 2 Strength).<br>&#8226; " +
				"May perform Poisonous Vomit, inflicting 2 Poison damage to D10 units, in its melee range. These units additionally suffer 2 Poison damage per turn, for 3 Combat rounds. 4 turns Cooldown.', 8, 'gargoyle')");
		db.execSQL("INSERT INTO Monster VALUES('Djinn', 'Elementae & Constructs', 'Elementa Caster', 25, 'Don’t Fuck With It', 000, 6, 000, '<br>&#8226; <b>Speed</b>: In-battle teleportation<br>&#8226; " +
				"<b>Range</b>: Practically unlimited<br>&#8226; " +
				"Receives half damage from non-magical attacks.<br>&#8226; " +
				"Receives no damage from Poison attacks.<br>&#8226; " +
				"Elemental attacks heal it instead of damaging it.<br>&#8226; " +
				"Attacks twice per combat round, with Fire/Thunder or Ice Bolt (D8+2 damage).<br>&#8226; " +
				"Dimeritium bombs block its movement and prevent it from taking two actions per turn -whether that is attacks or spells, etc-.<br>&#8226; " +
				"When its Health Points reach 10 or below, it can Cast Firestorm, Thunderstorm or Hailstorm once per combat round, dealing (D12+8) damage. This action counts as one attack, so before or after casting it, it can use its regular attack.<br>&#8226; " +
				"After being hit by 3 Elemental Attacks, Spells or Signs, it may cast Implosion once, dealing 10 damage to itself and (4D20 + 20) damage to its enemies.', 9, 'djinn')");
		// endregion

		// region insert Hybrids
		db.execSQL("INSERT INTO Monster VALUES('Manticore', 'Hybrids', 'Melee Hybrid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 1, 'manticore')");
		db.execSQL("INSERT INTO Monster VALUES('Griffin', 'Hybrids', 'Melee Hybrid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 2, 'griffin')");
		// endregion

		// region insert Insectoids
		db.execSQL("INSERT INTO Monster VALUES('Arachas', 'Insectoids', 'Melee Armoured Insectoid', 2, '1', 2, 0, 0, '<br>&#8226; 1 Armour<br>&#8226; " +
				"Deals 1 Poison damage, per combat round, to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives +50% Fire damage (round down)', 1, 'arachas')");
		db.execSQL("INSERT INTO Monster VALUES('Armoured Arachas', 'Insectoids', 'Melee Armoured Insectoid', 20, '10 (2 Dice x 5 Strength)', 2, 0, 0, '<br>&#8226; 15 Armour<br>&#8226; " +
				"Immune to Ranged attacks<br>&#8226; " +
				"Armour cannot be pierced/negated by any ability -with the exception of Ogroid attacks or Earth damage-<br>&#8226; " +
				"Deals 1 Poison damage, per combat round, to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives +50% Fire damage (round down)', 2, 'arachas_armoured')");
		db.execSQL("INSERT INTO Monster VALUES('Venomous Arachas', 'Insectoids', 'Melee/Ranged Insectoid', 20, '10 (5 Dice x 2 Strength)', 2, 0, 1, '<br>&#8226; 5 Armour<br>&#8226; " +
				"On round end, deals (D8+2) Poison damage to an adjacent tile that affects every stack of units on that tile<br>&#8226; " +
				"Deals 1 Poison damage, per combat round, to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives +50% Fire damage (round down)', 3, 'arachas_venomous')");
		db.execSQL("INSERT INTO Monster VALUES('Giant Centipede', 'Insectoids', 'Harassing Insectoid', 6, '6 (3 Dice x 2 Strength)', 3, 2, 0, '<br>&#8226; May perform Hit and Run, with the special perk of digging itself a tunnel when it runs away.<br>&#8226; " +
				"Completely immune to all attacks when it burrows underground.<br>&#8226; " +
				"Some Earth spells will kill it instantly during that phase.<br>&#8226; " +
				"Water and Ice attacks will paralyze it for 1 turn and Yrden sign for 3 turns.', 4, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Pale Widow', 'Insectoids', 'Disgusting Insectoid', 10, '9 (3 Dice x 3 Strength)', 4, 2, 0, '<br>&#8226; Can spit Venom with 1 Range, dealing 6 (2 x 3) Damage. This attack ignores Armour.<br>&#8226; " +
				"May perform Hit and Run, with the special perk of digging itself a tunnel when it runs away.<br>&#8226; " +
				"Completely immune to all attacks when it burrows underground.<br>&#8226; " +
				"Some Earth spells will kill it instantly during that phase.<br>&#8226; " +
				"Water and Ice attacks will paralyze it for 1 turn and Yrden sign for 3 turns.', 5, 'pale_widow')");
		db.execSQL("INSERT INTO Monster VALUES('Endrega Worker', 'Insectoids', 'Melee Insectoid', 2, '1', 2, 0, 0, '<br>&#8226; Receives +50% Fire damage (rounded down)', 6, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Endrega Drone', 'Insectoids', 'Ranged Insectoid', 3, '2', 2, 1, 1, '<br>&#8226; Deals Poison damage<br>&#8226; " +
				"Deals 1 Poison damage to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives +50% Fire damage (round down)', 7, 'endrega_drone')");
		db.execSQL("INSERT INTO Monster VALUES('Endrega Warrior', 'Insectoids', 'Melee Insectoid', 10, '10 (5 Dice x 2 Strength)', 2, 1, 0, '<br>&#8226; Its long Barbed Tail has great reach and a lethal venom. Its attacks ignore Armour and deal an additional 1 Poison damage to the target per combat round.<br>&#8226; " +
				"Receives +50% Fire damage (round down)', 8, 'endrega_warrior')");
		db.execSQL("INSERT INTO Monster VALUES('Kikimore Worker', 'Insectoids', 'Melee Insectoid', 2, '1', 2, 0, 0, '<br>&#8226; Immune to Poison and Earth damage, and to Mind-related spells and abilities.<br>&#8226; " +
				"Deals 1 Poison damage, per combat round, to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives triple damage from Fire attacks.', 9, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Kikimore Warrior', 'Insectoids', 'Melee Insectoid', 14, '6 (3 Dice x 2 Strength)', 2, 0, 0, '<br>&#8226; Immune to Poison and Earth damage, and to Mind-related spells and abilities.<br>&#8226; " +
				"Deals 1 Poison damage, per combat round, to any enemy it has previously hit. The Poison damage ignores armour, and this effect lasts until the victim is killed -or the battle is over-<br>&#8226; " +
				"Receives triple damage from Fire attacks.', 10, 'kikimore_warrior')");
		db.execSQL("INSERT INTO Monster VALUES('Kikimore Queen', 'Insectoids', 'Melee Boss Insectoid', 40, '22 (4 Dice x 4 Strength + 1 Dice x 6 Strength)', 1, 0, 0, '<br>&#8226; 80 Armour<br>&#8226; " +
				"Immune to Elemental damage (fire, water, etc.)', 11, 'kikimore_queen')");
		db.execSQL("INSERT INTO Monster VALUES('Koschey', 'Insectoids', 'Melee Construct/Insectoid', 50, '40 (10 Dice X 4 Strength)', 3, 0, 0, '<br>&#8226; 50 Armour<br>&#8226; " +
				"May attack Fortifications with 15 Strength (5 Dice X 3 Strength).<br>&#8226; " +
				"Immune to Mind-related effects.<br>&#8226; " +
				"Receives double damage from Fire and Water attacks.<br>&#8226; " +
				"Can only be conjured by a powerful mage, and will try to break free each turn. At the end of its turn, roll a D12 and add the number of turns that have passed since the creature was conjured.', 12, 'koschey')");
		// endregion

		// region insert Necrophages
		db.execSQL("INSERT INTO Monster VALUES('Ghoul', 'Necrophages', 'Melee Necrophage', 3, '2', 3, 0, 0, '<br>&#8226; Heals 1 Hit Point for every living enemy killed -max 1 Hit Point healed for every Necrophage-', 1, 'ghoul')");
		db.execSQL("INSERT INTO Monster VALUES('Alghoul', 'Necrophages', 'Melee Necrophage/Pack Leader', 10, '4', 3, 0, 0, '<br>&#8226; Heals 5 Hit Points per turn<br>&#8226; " +
				"Deals 2 damage for every point of melee damage received -unless under the influence of Mind-related spells or signs-', 2, 'alghoul')");
		db.execSQL("INSERT INTO Monster VALUES('Drowner', 'Necrophages', 'Melee Necrophage', 1, '1', 2, 0, 0, '<br>&#8226; Gains 1 Strength and 2 Initiative if fighting in a territory adjacent to Water<br>&#8226; " +
				"Receives double damage from Fire and Wind spells', 3, 'ic_menu_monsters')");
		db.execSQL("INSERT INTO Monster VALUES('Foglet', 'Necrophages', 'Melee Necrophage/Caster/Ambusher', 10, '12 (4 Dice x 3 Strength)', 2, 1, 0, '<br>&#8226; May perform Ambush<br>&#8226; " +
				"Can attack through the Illusionary Mist using its Incorporeal forms. When doing so, it cannot get attacked, unless hit by Wind or Earth attacks -which also clear away the mist for 1 turn-.<br>&#8226; " +
				"Moon Dust and Dimeritium will prevent it from using its Illusionary Mist -but Dimeritium will prevent the use of any spells in the battlefield-.', 4, 'foglet')");
		// endregion

		// region insert Ogroids
		db.execSQL("INSERT INTO Monster VALUES('Cyclops', 'Ogroids', 'Melee Ogroid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 1, 'cyclops')");
		db.execSQL("INSERT INTO Monster VALUES('Jotunn', 'Ogroids', 'Melee Ogroid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 2, 'jotunn')");
		db.execSQL("INSERT INTO Monster VALUES('Nekker', 'Ogroids', 'Melee Ogroid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 3, 'nekker')");
		db.execSQL("INSERT INTO Monster VALUES('Troll', 'Ogroids', 'Melee/Ranged Ogroid', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 4, 'troll')");
		// endregion

		// region insert Relicts
		db.execSQL("INSERT INTO Monster VALUES('Chort', 'Relicts', 'Melee Relict', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 1, 'chort')");
		db.execSQL("INSERT INTO Monster VALUES('Fiend', 'Relicts', 'Melee Relict/Caster', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 2, 'fiend')");
		db.execSQL("INSERT INTO Monster VALUES('Leshen', 'Relicts', 'Melee Relict/Caster', 20, '10 (5 Dice x 2 Strength)', 2, 0, 0, '<br>&#8226; While in battle, it can teleport to any row to attack. It can only teleport once per turn.<br>&#8226; " +
				"After the first combat round, the Leshen summons a pack of D12+4 wolves to fight alongside it. If at the end of the next turn there are less than 10 allied wolves on the field, it will repeat the process, and so on.<br>&#8226; " +
				"It can cast Entangling Roots, preventing the movement and any movement-related abilities of D6+4 units for 2 turns. The cooldown is 5 turns.<br>&#8226; " +
				"When fighting in Forests, it gains +10 HP, +4 Strength and summons D12+8 Wolves per turn.', 3, 'leshen')");
		// endregion

		// region insert Vampires
		db.execSQL("INSERT INTO Monster VALUES('Ekkimara', 'Vampires', 'Lower Vampire/Melee', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 1, 'ekkimara')");
		db.execSQL("INSERT INTO Monster VALUES('Fleder', 'Vampires', 'Lower Vampire/Melee', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 2, 'fleder')");
		db.execSQL("INSERT INTO Monster VALUES('Garkain', 'Vampires', 'Lower Vampire/Melee', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 3, 'garkain')");
		db.execSQL("INSERT INTO Monster VALUES('Katakan', 'Vampires', 'Higher Vampire/Melee', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 4, 'katakan')");
		db.execSQL("INSERT INTO Monster VALUES('Bruxa', 'Vampires', 'Higher Vampire/Melee', 0, '', 0, 0, 0, '<br>&#8226; 1 Armour', 4, 'bruxa')");
		// endregion

	}
}
