package studio.aroudhub.ticketing.docker;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class DockerSchemaSqlTest {

    @Test
    void concertCreatedAtColumn_usesDateTypeInDockerSchema() throws IOException {
        String schemaSql = Files.readString(Path.of("docker", "db", "init", "tables", "schema.sql"));

        assertThat(schemaSql).contains("created_at DATE NOT NULL");
    }
}
