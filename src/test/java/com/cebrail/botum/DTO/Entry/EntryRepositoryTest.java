package com.cebrail.botum.DTO.Entry;


import com.cebrail.botum.Model.Entry;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.assertj.core.api.Assertions;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;

@JdbcTest
class EntryRepositoryTest {

    @Autowired
    EntryRepository entryRepository;



    @Test
    void testGetEntryBy() {
        Entry entry = entryRepository.getEntryBy("kelime", "history", "1139629783");
        Assertions.assertThat("tarih").isEqualTo(entry.getAnlami());
    }

    @Test
    void getEntryById() {
        Entry entry = entryRepository.getEntryById(6L , "643574229");
        Assertions.assertThat(entry.getKelime()).isEqualTo("baby");
        Assertions.assertThat(entry.getAnlami()).isEqualTo("bebek");
    }

    @Test
    void getEntryByKelime() {
        Entry entry = entryRepository.getEntryByKelime("baby", "643574229");

        Assertions.assertThat(entry.getKelime()).isEqualTo("baby");
        Assertions.assertThat(entry.getAnlami()).isEqualTo("bebek");
    }

    @Test
    void getEntryByAnlam() {
        Entry entry = entryRepository.getEntryByAnlam("bebek", "643574229");
        Assertions.assertThat(entry.getKelime()).isEqualTo("baby");
    }

    @Test
    void getEntriesByUserChatId() {
        Assertions.assertThat(entryRepository.getEntriesByUserChatId("643574229").size())
                .isGreaterThan(1);
    }

    @Test
    void exists() {
        Assertions.assertThat(entryRepository.exists("baby", "643574229")).isEqualTo(true);
    }
}