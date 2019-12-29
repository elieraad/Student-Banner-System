package me.elieraad.sql;

public class GradeStats {
    private int males, females, lebanese, palestinians, syrians, others;
           String grade, nbSections;

    public GradeStats(String grade, String nbSections, int males, int females, int lebanese, int palestinians, int syrians, int others) {
        this.grade = grade;
        this.nbSections = nbSections;
        this.males = males;
        this.females = females;
        this.lebanese = lebanese;
        this.palestinians = palestinians;
        this.syrians = syrians;
        this.others = others;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNbSections() {
        return nbSections;
    }

    public void setNbSections(String nbSections) {
        this.nbSections = nbSections;
    }

    public int getMales() {
        return males;
    }

    public void setMales(int males) {
        this.males = males;
    }

    public int getFemales() {
        return females;
    }

    public void setFemales(int females) {
        this.females = females;
    }

    public int getLebanese() {
        return lebanese;
    }

    public void setLebanese(int lebanese) {
        this.lebanese = lebanese;
    }

    public int getPalestinians() {
        return palestinians;
    }

    public void setPalestinians(int palestinians) {
        this.palestinians = palestinians;
    }

    public int getSyrians() {
        return syrians;
    }

    public void setSyrians(int syrians) {
        this.syrians = syrians;
    }

    public int getOthers() {
        return others;
    }

    public void setOthers(int others) {
        this.others = others;
    }
}