package com.hamsoo.bpm.common.utils;

import com.hamsoo.bpm.common.exception.ServiceException;
import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Component
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    private final DataSource dataSource;

    public ReportGenerator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] exportReportToPdf(String resourceLocation, Map<String, Object> parameters) {
        byte[] content;
        try (Connection connection = dataSource.getConnection();
             InputStream reportInputStream = new ClassPathResource(resourceLocation).getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportInputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            content = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("error.reportGenerator.cannotConnectToDatabase");
        } catch (JRException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("error.reportGenerator.errorInGenerateReport");
        }
        return content;
    }

}
