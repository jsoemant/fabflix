
public class Actor {

    private String name;

    private String birthYear;

    public Actor() {

    }

    public Actor(String name, String birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String toString() {
        return "Name:" + getName() + ", " + "DOB:" + getBirthYear();
    }

    public static String parse(String o) {
        return o.toLowerCase().strip();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Actor))
            return false;
        Actor n = (Actor) o;

        String x1 = (n.name == null) ? n.name : parse(n.name);
        String y1 = (n.birthYear == null) ? n.birthYear : parse(n.birthYear);
        String x2 = (name == null) ? name : parse(name);
        String y2 = (birthYear == null) ? birthYear : parse(birthYear);

        if (birthYear == null || n.birthYear == null) {
            return x1.equals(x2) && (y1 == y2);
        }

        return x1.equals(x2) && y1.equals(y2);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        String x1 = (name == null) ? name : parse(name);
        String y1 = (birthYear == null) ? birthYear : parse(birthYear);

        result = prime * result + ((x1 == null) ? 0 : x1.hashCode());
        result = prime * result + ((y1 == null) ? 0 : y1.hashCode());
        return result;
    }

}