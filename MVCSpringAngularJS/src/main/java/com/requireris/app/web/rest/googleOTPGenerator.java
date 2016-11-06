package com.requireris.app.web.rest;

/**
 * Created by noboud_n on 06/11/2016.
 */

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class googleOTPGenerator {
    @RequestMapping(value = "/{moduleType}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody String uploadFile(@RequestParam("key") String key,
                                            @PathVariable String moduleType) {
        String code = "mdr";
        return new String(code);
    }

}
