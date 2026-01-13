package gr.registry.service;

import gr.registry.domain.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;


public class CitizenRepositoryTest {

    private static CitizenRepository repository;

    @BeforeAll
    public static void setup() {
        repository = new CitizenRepository();
    }
    
    @BeforeEach
    public void clearDatabase() {
        repository.findAll().forEach(c -> repository.delete(c.getIdNumber()));
    }


    @Test
    public void testSaveAndFindById() {
        Citizen citizen = new Citizen("AB123456", "Nikos", "Papadopoulos", Gender.MALE, "12-11-1995");
        repository.save(citizen);

        Citizen found = repository.findById("AB123456");
        assertNotNull(found);
        assertEquals("Nikos", found.getFirstName());
    }

    @Test
    public void testUpdateCitizen() {
        Citizen citizen = new Citizen("AB123456", "Nikos", "Papadopoulos", Gender.MALE, "12-11-1995");
        repository.save(citizen);

        citizen.updateOptionalFields("123456789", "Athens");
        repository.update(citizen);

        Citizen updated = repository.findById("AB123456");
        assertEquals("123456789", updated.getAfm());
        assertEquals("Athens", updated.getAddress());
    }

/*
    @Test
    public void testFindAll() {
        List<Citizen> citizens = repository.findAll();
        assertTrue(citizens.size() >= 1);
    }
*/
    @Test
    public void testFindAll() {
        
        repository.findAll().forEach(c -> repository.delete(c.getIdNumber()));

       
        Citizen citizen = new Citizen("AB999999", "Maria", "Ioannou", Gender.FEMALE, "05-07-1990");
        repository.save(citizen);

       
        List<Citizen> citizens = repository.findAll();
        assertFalse(citizens.isEmpty(), "Η λίστα πολιτών δεν πρέπει να είναι άδεια");
        assertEquals(1, citizens.size());
        assertEquals("Maria", citizens.get(0).getFirstName());
    }

    
    @Test
    public void testDeleteCitizen() {
        repository.delete("AB123456");
        assertNull(repository.findById("AB123456"));
    }
}
