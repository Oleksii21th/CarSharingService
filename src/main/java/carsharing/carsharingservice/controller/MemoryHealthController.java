package carsharing.carsharingservice.controller;

import carsharing.carsharingservice.healthcheck.MemoryHealthIndicator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemoryHealthController {

    private final MemoryHealthIndicator memoryHealthIndicator;

    public MemoryHealthController(MemoryHealthIndicator memoryHealthIndicator) {
        this.memoryHealthIndicator = memoryHealthIndicator;
    }

    @Operation(summary = "Returns current memory usage and health information")
    @GetMapping("/memory-health")
    public Health getMemoryHealth() {
        return memoryHealthIndicator.health();
    }
}
