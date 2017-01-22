package tie.hackathon.travelguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import tie.hackathon.travelguide.tables.EFriend;
import tie.hackathon.travelguide.tables.ETables;
import tie.hackathon.travelguide.tables.FriendDb;
import tie.hackathon.travelguide.tables.PodrozDB;
import tie.hackathon.travelguide.tables.TabPodroz;

public class ZarzadcaBazy extends SQLiteOpenHelper {

	public ZarzadcaBazy(Context context) {
		super(context, "travel.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(
				"create table podroz(" +
				"id integer primary key autoincrement," +
				 TabPodroz.NAZWA.name() + " text," +
				TabPodroz.DATA_START.name() + " text," +
				TabPodroz.MIASTO.name() + " text," +
				TabPodroz.OPIS.name() + " text," +
				TabPodroz.DATA_KONIEC.name() + " text);");

		db.execSQL(
				"create table friend(" +
						"id integer primary key autoincrement," +
						EFriend.PODROZ_ID.name() + " integer," +
						EFriend.NAME.name() + " text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void dodajPodroz(String nazwa, String cityname, String startdate, String endDate, String opis){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TabPodroz.NAZWA.name(), nazwa);
		values.put(TabPodroz.MIASTO.name(), cityname);
		values.put(TabPodroz.DATA_START.name(), startdate);
		values.put(TabPodroz.DATA_KONIEC.name(), endDate);
		values.put(TabPodroz.OPIS.name(), opis);
		db.insertOrThrow("podroz", null, values);
	}

	public void dodajFriend(String nazwa, int podrozId){
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(EFriend.NAME.name(), nazwa);
		values.put(EFriend.PODROZ_ID.name(), podrozId);
		db.insertOrThrow(ETables.FRIEND.name(), null, values);
	}


	public Cursor getAllPodroz(){
		String[] kolumny = getColumnsPodrozDB();
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor =db.query("podroz",kolumny,null,null,null,null,null);
		return kursor;
	}

	public Cursor getAllFriend(){
		String[] kolumny = getColumnsFriendDB();
		SQLiteDatabase db = getReadableDatabase();
		Cursor kursor =db.query(ETables.FRIEND.name(),kolumny,null,null,null,null,null);
		return kursor;
	}

	@NonNull
	private String[] getColumnsPodrozDB() {
		return new String[]{TabPodroz.NAZWA.name(),TabPodroz.DATA_START.name(),TabPodroz.DATA_KONIEC.name(), TabPodroz.ID.name(), TabPodroz.MIASTO.name(), TabPodroz.OPIS.name()};
	}

	@NonNull
	private String[] getColumnsFriendDB() {
		return new String[]{EFriend.ID.name(), EFriend.NAME.name(), EFriend.PODROZ_ID.name()};
	}

	public List<PodrozDB> getAllTrips(){
		List<PodrozDB> podrozDBList = new ArrayList<>();
		Cursor c = getAllPodroz();
		while(c.moveToNext()){
			PodrozDB podrozDB = new PodrozDB();
			podrozDB.setNazwa(c.getString(0));
			podrozDB.setDataStart(c.getString(1));
			podrozDB.setDataKoniec(c.getString(2));
			podrozDB.setId(c.getInt(3));
			podrozDB.setMiasto(c.getString(4));
			podrozDBList.add(podrozDB);
		}
		return podrozDBList;
	}

	@NonNull
	private PodrozDB getPodrozDBByCursor(Cursor c) {
		PodrozDB podrozDB = new PodrozDB();
		if(c != null){
			c.moveToFirst();
			podrozDB.setNazwa(c.getString(0));
			podrozDB.setDataStart(c.getString(1));
			podrozDB.setDataKoniec(c.getString(2));
			podrozDB.setId(c.getInt(3));
			podrozDB.setMiasto(c.getString(4));
		}
		return podrozDB;
	}

	@NonNull
	private FriendDb getFriendDBByCursor(Cursor c) {
		FriendDb friendDb = new FriendDb();
		if(c != null){
			c.moveToFirst();
			friendDb.setName(c.getString(1));
		}
		return friendDb;
	}

	public PodrozDB getPodroz(int id){
		PodrozDB podrozDB = new PodrozDB();
		SQLiteDatabase db = getReadableDatabase();
		String args[] = {id + ""};
		Cursor c =db.query("podroz",getColumnsPodrozDB(),"id=?",args,null,null,null);
		if(c != null){
			c.moveToFirst();
			podrozDB.setNazwa(c.getString(0));
			podrozDB.setDataStart(c.getString(1));
			podrozDB.setDataKoniec(c.getString(2));
			podrozDB.setId(c.getInt(4));
			podrozDB.setMiasto(c.getString(4));
			podrozDB.setOpis(c.getString(5));
		}
		return podrozDB;
	}

	public List<FriendDb> getAllFriendsByPodrozId(int id){
		List<FriendDb> friendDbs = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		String args[] = {id + ""};
		Cursor c =db.query(ETables.FRIEND.name(),getColumnsFriendDB(),EFriend.PODROZ_ID.name() + "=?",args,null,null,null);
		while (c.moveToNext()){
			FriendDb f = new FriendDb();
			f.setName(c.getString(1));
			friendDbs.add(f);
		}
		return friendDbs;
	}

	
}
