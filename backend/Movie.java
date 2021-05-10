import java.util.ArrayList;

public class Movie {
    private String title;
    private String director;
    private String year;
    private ArrayList<String> genres;

    public Movie() {
        this.genres = new ArrayList<String>();
    }

    public Movie(String title, String director, String year) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.genres = new ArrayList<String>();
    }

    public String getTitle() {
        return this.title;
    }

    public String getYear() {
        return this.year;
    }

    public ArrayList<String> getGenres() { return this.genres; }

    public String getDirector() { return this.director; }

    public void setTitle(String title) { this.title = title; }

    public void setDirector(String director) { this.director = director; }

    public void setYear(String year) { this.year = year; }

    public void addGenre(String genre) { this.genres.add(genre); }

    public static String parse(String o) {
        return o.toLowerCase().strip();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Movie))
            return false;
        Movie n = (Movie) o;

        String x1 = parse(n.title);
        String y1 = (n.year == null) ? null : parse(n.year);
        String z1 = parse(n.director);
        String x2 = parse(title);
        String y2 = (year == null) ? null : parse(year);
        String z2 = parse(director);

        if (y1 == null || y2 == null) {
            return x1.equals(x2) && y1 == y2 && z1.equals(z2);
        }

        return x1.equals(x2) && y1.equals(y2) && z1.equals(z2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        String x1 = parse(title);
        String y1 = (year == null) ? null : parse(year);
        String z1 = parse(director);

        result = prime * result + ((x1 == null) ? 0 : x1.hashCode());
        result = prime * result + ((y1 == null) ? 0 : y1.hashCode());
        result = prime * result + ((z1 == null) ? 0 : z1.hashCode());
        return result;
    }
}