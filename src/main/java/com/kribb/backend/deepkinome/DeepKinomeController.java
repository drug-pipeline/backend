package com.kribb.backend.deepkinome;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeepKinomeController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://str.kribb.re.kr/deepkinome/api";

    private ResponseEntity<String> proxyGet(String path, Map<String, String> queryParams) {
        UriComponentsBuilder b = UriComponentsBuilder.fromHttpUrl(BASE_URL + path);
        if (queryParams != null) queryParams.forEach(b::queryParam);
        URI uri = b.build(true).toUri();

        try {
            var resp = restTemplate.getForEntity(uri, String.class);
            return ResponseEntity.status(resp.getStatusCode()).body(resp.getBody());
        } catch (RestClientResponseException ex) {
            var status = HttpStatus.resolve(ex.getRawStatusCode());
            return ResponseEntity.status(status != null ? status : HttpStatus.BAD_GATEWAY)
                    .body(ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Proxy error: " + ex.getMessage());
        }
    }

    @GetMapping("/deepkinome/predictions/{taskId}")
    public ResponseEntity<String> predictions(@PathVariable String taskId,
                                              @RequestParam(required = false) Map<String, String> query) {
        return proxyGet("/predictions/" + taskId, query);
    }

    @GetMapping("/deepkinome/admet/{taskId}")
    public ResponseEntity<String> admet(@PathVariable String taskId,
                                        @RequestParam(required = false) Map<String, String> query) {
        return proxyGet("/admet/" + taskId, query);
    }

    @GetMapping("/deepkinome/smiles/{taskId}")
    public ResponseEntity<String> smiles(@PathVariable String taskId,
                                         @RequestParam(required = false) Map<String, String> query) {
        return proxyGet("/smiles/" + taskId, query);
    }

    @GetMapping("/deepkinome/docking/{taskId}")
    public ResponseEntity<String> docking(@PathVariable String taskId,
                                          @RequestParam(required = false) Map<String, String> query) {
        return proxyGet("/docking/" + taskId, query);
    }

    @GetMapping("/deepkinome/matching/{taskId}")
    public ResponseEntity<String> matching(@PathVariable String taskId,
                                           @RequestParam(required = false) Map<String, String> query) {
        return proxyGet("/matching/" + taskId, query);
    }

    // ===== New UniProt endpoints =====
    @GetMapping("/deepkinome/uniprot")
    public ResponseEntity<String> uniprot(@RequestParam(required = false) Map<String, String> query) {
        // expects "?id=<uniprotId>"
        return proxyGet("/uniprot", query);
    }

    @GetMapping("/deepkinome/uniprot/structure")
    public ResponseEntity<String> uniprotStructure(@RequestParam(required = false) Map<String, String> query) {
        // expects "?id=<uniprotId>"
        return proxyGet("/uniprot/structure", query);
    }

    @GetMapping("/deepkinome/uniprot/features")
    public ResponseEntity<String> uniprotFeatures(@RequestParam(required = false) Map<String, String> query) {
        // expects "?id=<uniprotId>"
        return proxyGet("/uniprot/features", query);
    }

    // ===== New PDB endpoint =====
    @GetMapping("/deepkinome/pdb")
    public ResponseEntity<String> pdb(@RequestParam(required = false) Map<String, String> query) {
        // expects "?id=<pdbId>"
        return proxyGet("/pdb", query);
    }
}
