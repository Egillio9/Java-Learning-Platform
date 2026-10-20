package com.himanshu.javalearn.judge;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * @param timeLimit      wall-clock budget for one batch, including JVM start-up
 * @param memoryMb       heap cap for the child JVM
 * @param maxConcurrent  child JVMs allowed at once (each needs ~100 MB)
 * @param maxOutputBytes stdout bytes read before the run is killed
 */
@ConfigurationProperties(prefix = "judge")
public record JudgeProperties(Duration timeLimit, int memoryMb, int maxConcurrent, int maxOutputBytes,
                              int maxSourceChars) {

    public JudgeProperties {
        if (timeLimit == null) timeLimit = Duration.ofSeconds(8);
        if (memoryMb <= 0) memoryMb = 96;
        if (maxConcurrent <= 0) maxConcurrent = 1;
        if (maxOutputBytes <= 0) maxOutputBytes = 1_000_000;
        if (maxSourceChars <= 0) maxSourceChars = 20_000;
    }
}
