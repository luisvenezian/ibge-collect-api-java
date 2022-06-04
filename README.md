# api to avro

Java for collecting data from API 

By the time what we have here is a Java class that requests a public API with cities data from Brazil, that class also writes the output in avro format with snappy compression. 

Why avro? Because avro is faster, schema flexible (as you can see municipios.avsc file) and thinking about data solutions escaling: A simple dataset JSON with brazil cities like this one may have 6.3 MB of data, that volume after conversion to avro plus snappy compression comes to 150 KB. Sounds good, doesn't it?

Source: [IBGE API](https://servicodados.ibge.gov.br/api/docs/localidades#api-Municipios-municipiosGet)   
Destination: local file municipios.avro.snappy
