package tie.hackathon.travelguide.tables;

/**
 * Created by Mariusz on 21.01.2017.
 */

public class PodrozDB {

    private Integer id;
    private String dataStart;
    private String dataKoniec;
    private String nazwa;
    private String miasto;
    private String opis;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataStart() {
        return dataStart;
    }

    public void setDataStart(String dataStart) {
        this.dataStart = dataStart;
    }

    public String getDataKoniec() {
        return dataKoniec;
    }

    public void setDataKoniec(String dataKoniec) {
        this.dataKoniec = dataKoniec;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getMiasto() {
        return miasto;
    }

    public void setMiasto(String miasto) {
        this.miasto = miasto;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
