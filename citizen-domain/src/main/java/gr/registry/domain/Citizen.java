package gr.registry.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "citizens")
public class Citizen {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Id
    private String idNumber;          // ΑΤ
    private String firstName;
    private String lastName;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(name = "birth_date")
    private LocalDate birthDate;
    private String afm;               // optional
    private String address;           // optional

    public Citizen() {
    }
    
    public Citizen(String idNumber,
                   String firstName,
                   String lastName,
                   Gender gender,
                   String birthDate) {

        validateIdNumber(idNumber);
        validateRequired(firstName, "Όνομα");
        validateRequired(lastName, "Επίθετο");
        Objects.requireNonNull(gender, "Το φύλο είναι υποχρεωτικό");
        this.birthDate = parseBirthDate(birthDate);

        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    /* ================= VALIDATION ================= */

    private void validateIdNumber(String idNumber) {
        if (idNumber == null || !idNumber.matches("\\w{8}")) {
            throw new ValidationException("Ο ΑΤ πρέπει να αποτελείται από 8 χαρακτήρες");
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " είναι υποχρεωτικό πεδίο");
        }
    }

    private LocalDate parseBirthDate(String value) {
        try {
            return LocalDate.parse(value, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ValidationException(
                    "Η ημερομηνία γέννησης πρέπει να είναι της μορφής dd-MM-yyyy");
        }
    }

    /* ================= UPDATE METHODS ================= */

    public void updateOptionalFields(String afm, String address) {
        if (afm != null) {
            validateAfm(afm);
            this.afm = afm;
        }
        if (address != null) {
            this.address = address;
        }
    }

    private void validateAfm(String afm) {
        if (!afm.matches("\\d{9}")) {
            throw new ValidationException("Το ΑΦΜ πρέπει να έχει 9 ψηφία");
        }
    }

    /* ================= GETTERS ================= */

    public String getIdNumber() {
        return idNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getAfm() {
        return afm;
    }

    public String getAddress() {
        return address;
    }
    
    /* ================= SETTERS ================= */
    
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}


