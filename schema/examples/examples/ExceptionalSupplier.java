var response = ExceptionalSupplier.of(() -> fetchData())
    .with(ex -> log.warn("fetch failed", ex))
    .execute();
if (response.wasNoError()) {
    return response.response();
}
