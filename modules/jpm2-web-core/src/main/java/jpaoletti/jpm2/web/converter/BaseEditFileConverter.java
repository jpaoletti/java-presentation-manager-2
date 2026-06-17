package jpaoletti.jpm2.web.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import jpaoletti.jpm2.core.converter.Converter;
import jpaoletti.jpm2.core.exception.ConfigurationException;
import jpaoletti.jpm2.core.exception.ConverterException;
import jpaoletti.jpm2.core.exception.FieldNotFoundException;
import jpaoletti.jpm2.core.exception.IgnoreConvertionException;
import jpaoletti.jpm2.core.model.ContextualEntity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.core.model.WithAttachment;
import jpaoletti.jpm2.util.JPMUtils;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jpaoletti
 */
public abstract class BaseEditFileConverter extends Converter {

    protected String measure = "k";
    protected String accept = "'@'"; // /(\.|\/)(gif|jpe?g|png)$/i
    protected String filenameField = null;

    @Override
    public Object visualizeValue(ContextualEntity contextualEntity, Field field, Object instance, Object object, String instanceId) throws ConverterException, ConfigurationException {
        try {
            byte[] value = (byte[]) object;
            if (value == null && instance instanceof WithAttachment) {
                WithAttachment wa = (WithAttachment) instance;
                try {
                    value = FileUtils.readFileToByteArray(new File(wa.getInternalFileName()));
                } catch (IOException ex) {
                    value = null;
                }
            }
            if (value != null && value.length > 0) {
                String len;
                switch (getMeasure().charAt(0)) {
                    case 'b':
                        len = value.length + " bytes";
                        break;
                    case 'm':
                        len = (value.length / 1024 / 1024) + " Mb";
                        break;
                    case 'k':
                        ;
                    default:
                        len = (value.length / 1024) + "Kb";
                }
                return getPage(contextualEntity, field, object, instanceId) + "delete=true&len=" + len + "&accept=" + getAccept();
            } else {
                return getPage(contextualEntity, field, object, instanceId) + "delete=false&accept=" + getAccept();
            }
        } catch (ClassCastException e) {
            throw new ConverterException("jpm.converter.file.error.not.file");
        }
    }

    @Override
    public Object build(ContextualEntity contextualEntity, Field field, Object object, Object newValue) throws ConverterException {
        if (newValue != null && newValue.equals("@current:")) {
            throw new IgnoreConvertionException();
        }
        if (newValue == null || newValue.equals("")) {
            return null;
        }
        final File file = new File(String.valueOf(newValue)); //Tmp path
        // Security: newValue must be a temp file produced by the upload endpoint
        // (/jpm/uploadFileConverter), never an arbitrary server path supplied by the
        // client. Without this check a form field can point to any readable file on
        // the server (e.g. /opt/.../database.properties) and exfiltrate its content
        // through the image blob. See JPMController.uploadFileConverter.
        if (!isUploadedTempFile(file)) {
            JPMUtils.getLogger().error("Rejected file path outside upload temp dir in file converter: " + newValue);
            throw new ConverterException("jpm.error.uploading.file");
        }
        if (!file.exists()) {
            JPMUtils.getLogger().error("File not exists in file converter");
            throw new ConverterException("jpm.error.uploading.file");
        }
        byte[] bFile = new byte[(int) file.length()];
        try (final FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(bFile);
            if (getFilenameField() != null) {
                final Field f = contextualEntity.getEntity().getFieldById(getFilenameField(), contextualEntity.getContext());
                JPMUtils.set(object, f.getProperty(), file.getName());
            }
        } catch (IOException ex) {
            JPMUtils.getLogger().error("IOException in file converter ", ex);
            throw new ConverterException("jpm.error.uploading.file");
        } catch (FieldNotFoundException ex) {
            JPMUtils.getLogger().error("FieldNotFoundException in file converter ", ex);
            throw new ConverterException("jpm.error.uploading.file");
        }
        return bFile;
    }

    /**
     * Validates that the given file is one that was just uploaded through the JPM
     * upload endpoint. {@code JPMController.uploadFileConverter} stores uploads as
     * {@code <java.io.tmpdir>/jpm<random>/<originalFilename>}, so a legitimate value
     * is always a file whose parent directory name starts with {@code "jpm"} and
     * lives directly under the system temp directory. The path is canonicalized
     * first so {@code ..} segments and symlinks cannot be used to escape the temp
     * directory and read arbitrary files on the server.
     */
    protected boolean isUploadedTempFile(File file) {
        try {
            final File tmpRoot = new File(System.getProperty("java.io.tmpdir")).getCanonicalFile();
            final File canonical = file.getCanonicalFile();
            final File parent = canonical.getParentFile();
            return parent != null
                    && parent.getName().startsWith("jpm")
                    && tmpRoot.equals(parent.getParentFile());
        } catch (IOException ex) {
            JPMUtils.getLogger().error("Cannot resolve uploaded file path", ex);
            return false;
        }
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getFilenameField() {
        return filenameField;
    }

    public void setFilenameField(String filenameField) {
        this.filenameField = filenameField;
    }

    protected abstract String getPage(ContextualEntity contextualEntity, Field field, Object object, String instanceId);

}
