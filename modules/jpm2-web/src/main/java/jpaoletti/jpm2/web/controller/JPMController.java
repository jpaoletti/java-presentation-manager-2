package jpaoletti.jpm2.web.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import jpaoletti.jpm2.core.model.Entity;
import jpaoletti.jpm2.core.model.Field;
import jpaoletti.jpm2.util.JPMUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author jpaoletti
 */
@Controller
public class JPMController extends BaseController {

    @RequestMapping(value = "/jpm", method = RequestMethod.GET)
    public ModelAndView jpmStatus() {
        final ModelAndView mav = new ModelAndView("jpm-status");
        mav.addObject("jpm", getJpm());
        return mav;
    }

    @RequestMapping(value = "/jpm/uploadFileConverter", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public UploadFileResults uploadFileConverter(@RequestParam MultipartFile file) throws IOException {
        final Path tmpDir = Files.createTempDirectory("jpm");
        final String tmpFileName = tmpDir + File.separator + file.getOriginalFilename();
        final File tmpFile = new File(tmpFileName);
        tmpFile.deleteOnExit();
        file.transferTo(tmpFile);
        final UploadFileResults res = new UploadFileResults();
        res.getFiles().add(new UploadFileResult(tmpFileName, file.getContentType(), tmpFile.length()));
        return res;
    }

    @ResponseBody
    @RequestMapping(value = "/static/img/{entity}-{field}-{id}.png", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] showImageConverter(@PathVariable String entity, @PathVariable String field, @PathVariable String id, HttpServletResponse response) {
        try {
            final Entity e = getJpm().getEntity(entity);
            final Field f = e.getFieldById(field, "");
            return (byte[]) JPMUtils.get(e.getDao().get(id), f.getProperty());
        } catch (Exception e) {
            return null;
        }
    }

    public static class UploadFileResult {

        private String url;
        private String thumbnail_url;
        private String name;
        private String type;
        private Long size;
        private String delete_url;
        private String delete_type;

        public UploadFileResult(String name, String type, Long size) {
            this.name = name;
            this.type = type;
            this.size = size;
            this.url = "";
            this.thumbnail_url = "";
            this.delete_url = "";
            this.delete_type = "DELETE";
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumbnail_url() {
            return thumbnail_url;
        }

        public void setThumbnail_url(String thumbnail_url) {
            this.thumbnail_url = thumbnail_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public String getDelete_url() {
            return delete_url;
        }

        public void setDelete_url(String delete_url) {
            this.delete_url = delete_url;
        }

        public String getDelete_type() {
            return delete_type;
        }

        public void setDelete_type(String delete_type) {
            this.delete_type = delete_type;
        }
    }

    public static class UploadFileResults {

        private List<UploadFileResult> files;

        public UploadFileResults() {
            this.files = new ArrayList<>();
        }

        public List<UploadFileResult> getFiles() {
            return files;
        }

        public void setFiles(List<UploadFileResult> files) {
            this.files = files;
        }
    }
}
