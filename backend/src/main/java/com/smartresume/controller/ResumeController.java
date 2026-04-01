@PostMapping("/api/process")
@CrossOrigin(origins = "*")
public Map<String, Object> processResumes(
        @RequestParam("pdf_docs") List<MultipartFile> files,
        @RequestParam(value = "job_title", required = false) String jobTitle,
        @RequestParam(value = "job_desc", required = false) String jobDesc
) {
    Map<String, Object> response = new HashMap<>();

    response.put("jobTitle", jobTitle);
    response.put("totalResumes", files.size());

    List<Map<String, Object>> resumes = new ArrayList<>();

    for (MultipartFile file : files) {
        Map<String, Object> r = new HashMap<>();
        r.put("name", file.getOriginalFilename());
        r.put("score", (int)(Math.random() * 100)); // dummy score
        resumes.add(r);
    }

    response.put("resumes", resumes);

    return response;
}