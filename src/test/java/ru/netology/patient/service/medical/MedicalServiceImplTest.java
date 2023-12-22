package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.ArgumentCaptor;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class MedicalServiceImplTest {

    PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
    SendAlertService alertService = Mockito.mock(SendAlertService.class);

    @Test
    void checkBloodPressureIfBad() {

        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("testId", "testName", "testSurname",
                        LocalDate.of(1900, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));

        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl tmp = new MedicalServiceImpl(patientInfoRepository, alertService);
        tmp.checkBloodPressure("testId", new BloodPressure(225, 178));

        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(alertService).send(argumentCaptor.capture());

        assertThat("Warning, patient with id: testId, need help", is(argumentCaptor.getValue()));

    }

    @Test
    void checkBloodPressureIfGood() {

        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("testId", "testName", "testSurname",
                        LocalDate.of(1900, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));

        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl tmp = new MedicalServiceImpl(patientInfoRepository, alertService);
        tmp.checkBloodPressure("testId", new BloodPressure(125, 78));

        Mockito.verify(alertService, Mockito.never()).send(Mockito.anyString());

    }

    @ParameterizedTest
    @CsvSource(value = {
            "33.3, 1",
            "36.6, 0"
    })
    void checkTemperature(String temp, int times) {

        Mockito.when(patientInfoRepository.getById(Mockito.anyString()))
                .thenReturn(new PatientInfo("testId", "testName", "testSurname",
                        LocalDate.of(1900, 1, 1),
                        new HealthInfo(new BigDecimal("36.6"), new BloodPressure(125, 78))));

        Mockito.doNothing().when(alertService).send(Mockito.anyString());

        MedicalServiceImpl tmp = new MedicalServiceImpl(patientInfoRepository, alertService);

        tmp.checkTemperature("testId", new BigDecimal(temp));

        Mockito.verify(alertService, Mockito.times(times)).send(Mockito.anyString());

    }
}