public class StarMovie {
    private String star;
    private String movie;
    private String director;

    public StarMovie() {

    }

    public StarMovie(String star, String movie, String director) {
        this.star = star;
        this.movie = movie;
        this.director = director;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getStar() {
        return star;
    }

    public String getDirector() {
        return director;
    }

    public String getMovie() {
        return movie;
    }

    public static String parse(String o) {
        return o.toLowerCase().strip();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StarMovie))
            return false;
        StarMovie n = (StarMovie) o;

        String x1 = parse(n.star);
        String y1 = parse(n.director);
        String z1 = parse(n.movie);
        String x2 = parse(star);
        String y2 = parse(director);
        String z2 = parse(n.movie);

        return x1.equals(x2) && y1.equals(y2) && z1.equals(z2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        String x1 = parse(star);
        String y1 = parse(director);
        String z1 = parse(movie);

        result = prime * result + ((x1 == null) ? 0 : x1.hashCode());
        result = prime * result + ((y1 == null) ? 0 : y1.hashCode());
        result = prime * result + ((z1 == null) ? 0 : z1.hashCode());
        return result;
    }

}