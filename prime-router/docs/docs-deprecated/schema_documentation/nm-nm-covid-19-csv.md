### Schema: nm/nm-covid-19-csv
### Topic: covid-19
### Tracking Element: (message_id)
### Base On: [covid-19](./covid-19.md)
### Extends: none
#### Description: New Mexico Flat File

---

**Name**: EmpHealthcare

**ReportStream Internal Name**: employed_in_healthcare

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 95418-0

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Is the patient employed in health care?
---

**Name**: FirstTest

**ReportStream Internal Name**: first_test

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 95417-2

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Is this the patient's first test for this condition?
---

**Name**: Hospitalized

**ReportStream Internal Name**: hospitalized

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 77974-4

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Is the patient hospitalized?
---

**Name**: ICU

**ReportStream Internal Name**: icu

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 95420-6

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Is the patient in the ICU?
---

**Name**: DateTimeMessage

**ReportStream Internal Name**: local_file_created_date

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

---

**Name**: PtEmpAddrCity

**ReportStream Internal Name**: local_patient_employer_city

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpcounty

**ReportStream Internal Name**: local_patient_employer_county

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpName

**ReportStream Internal Name**: local_patient_employer_name

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpPhone

**ReportStream Internal Name**: local_patient_employer_phone

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpAddrState

**ReportStream Internal Name**: local_patient_employer_state

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpAddrStreet

**ReportStream Internal Name**: local_patient_employer_street

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtEmpAddrZip

**ReportStream Internal Name**: local_patient_employer_zip

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtLanguage

**ReportStream Internal Name**: local_patient_language

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: Occupation

**ReportStream Internal Name**: local_patient_occupation

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchAddrCity

**ReportStream Internal Name**: local_patient_school_city

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchcounty

**ReportStream Internal Name**: local_patient_school_county

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchoolName

**ReportStream Internal Name**: local_patient_school_name

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchAddrState

**ReportStream Internal Name**: local_patient_school_state

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchAddrStreet

**ReportStream Internal Name**: local_patient_school_street

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSchAddrZip

**ReportStream Internal Name**: local_patient_school_zip

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: PtSSN

**ReportStream Internal Name**: local_patient_ssn

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: ResultNotes

**ReportStream Internal Name**: local_result_notes

**Type**: TEXT

**PII**: No

**Default Value**: 

**Cardinality**: [0..1]

---

**Name**: ResultTestDesc

**ReportStream Internal Name**: local_result_test_desc

**Type**: TABLE

**PII**: No

**Cardinality**: [0..1]

**Table**: LIVD-SARS-CoV-2

**Table Column**: Component

---

**Name**: Result

**ReportStream Internal Name**: local_test_result

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
260373001|Detected|SNOMED_CT
260415000|Not detected|SNOMED_CT
720735008|Presumptive positive|SNOMED_CT
10828004|Positive|SNOMED_CT
42425007|Equivocal|SNOMED_CT
260385009|Negative|SNOMED_CT
895231008|Not detected in pooled specimen|SNOMED_CT
462371000124108|Detected in pooled specimen|SNOMED_CT
419984006|Inconclusive|SNOMED_CT
125154007|Specimen unsatisfactory for evaluation|SNOMED_CT
455371000124106|Invalid result|SNOMED_CT
840539006|Disease caused by sever acute respiratory syndrome coronavirus 2 (disorder)|SNOMED_CT
840544004|Suspected disease caused by severe acute respiratory coronavirus 2 (situation)|SNOMED_CT
840546002|Exposure to severe acute respiratory syndrome coronavirus 2 (event)|SNOMED_CT
840533007|Severe acute respiratory syndrome coronavirus 2 (organism)|SNOMED_CT
840536004|Antigen of severe acute respiratory syndrome coronavirus 2 (substance)|SNOMED_CT
840535000|Antibody to severe acute respiratory syndrome coronavirus 2 (substance)|SNOMED_CT
840534001|Severe acute respiratory syndrome coronavirus 2 vaccination (procedure)|SNOMED_CT
373121007|Test not done|SNOMED_CT
82334004|Indeterminate|SNOMED_CT

---

**Name**: OrdFacCity

**ReportStream Internal Name**: ordering_facility_city

**Type**: CITY

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The city of the facility which the test was ordered from
---

**Name**: OrdFacName

**ReportStream Internal Name**: ordering_facility_name

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The name of the facility which the test was ordered from
---

**Name**: OrdFacPhone

**ReportStream Internal Name**: ordering_facility_phone_number

**Type**: TELEPHONE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The phone number of the facility which the test was ordered from
---

**Name**: OrdFacState

**ReportStream Internal Name**: ordering_facility_state

**Type**: TABLE

**PII**: No

**Cardinality**: [1..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:
The state of the facility which the test was ordered from
---

**Name**: OrdFacAddrStreet

**ReportStream Internal Name**: ordering_facility_street

**Type**: STREET

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The address of the facility which the test was ordered from
---

**Name**: OrdFacZip

**ReportStream Internal Name**: ordering_facility_zip_code

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The zip code of the facility which the test was ordered from
---

**Name**: OrdProvFirstName

**ReportStream Internal Name**: ordering_provider_first_name

**Type**: PERSON_NAME

**PII**: No

**HL7 Fields**

- [OBR-16-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.16.3)
- [ORC-12-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.12.3)

**Cardinality**: [0..1]

**Documentation**:
The first name of the provider who ordered the test
---

**Name**: OrdProvLastName

**ReportStream Internal Name**: ordering_provider_last_name

**Type**: PERSON_NAME

**PII**: No

**HL7 Fields**

- [OBR-16-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.16.2)
- [ORC-12-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.12.2)

**Cardinality**: [0..1]

**Documentation**:
The last name of provider who ordered the test
---

**Name**: OrdProvPhone

**ReportStream Internal Name**: ordering_provider_phone_number

**Type**: TELEPHONE

**PII**: Yes

**HL7 Fields**

- [OBR-17](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.17)
- [ORC-14](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/ORC.14)

**Cardinality**: [0..1]

**Documentation**:
The phone number of the provider
---

**Name**: PtAddrCity

**ReportStream Internal Name**: patient_city

**Type**: CITY

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:
The patient's city
---

**Name**: PtCounty

**ReportStream Internal Name**: patient_county

**Type**: TABLE_OR_BLANK

**PII**: No

**Cardinality**: [1..1]

**Table**: fips-county

**Table Column**: County

---

**Name**: PtDOB

**ReportStream Internal Name**: patient_dob

**Type**: DATE

**PII**: Yes

**Format**: yyyyMMdd

**Cardinality**: [0..1]

**Documentation**:
The patient's date of birth. Default format is yyyyMMdd.

Other states may choose to define their own formats.

---

**Name**: PtEmailAdd

**ReportStream Internal Name**: patient_email

**Type**: EMAIL

**PII**: Yes

**Cardinality**: [0..1]

---

**Name**: PtEthnic

**ReportStream Internal Name**: patient_ethnicity

**Type**: CODE

**PII**: No

**Format**: use value found in the Code column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
H|Hispanic or Latino|HL7
N|Non Hispanic or Latino|HL7
U|Unknown|HL7

**Documentation**:
The patient's ethnicity. There is a valueset defined based on the values in PID-22, but downstream
consumers are free to define their own values. Please refer to the consumer-specific schema if you have questions.

---

**Name**: PtFirstName

**ReportStream Internal Name**: patient_first_name

**Type**: PERSON_NAME

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:
The patient's first name
---

**Name**: PtSex

**ReportStream Internal Name**: patient_gender

**Type**: CODE

**PII**: No

**Format**: use value found in the Code column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
M|Male|HL7
F|Female|HL7
O|Other|HL7
A|Ambiguous|HL7
U|Unknown|HL7
N|Not applicable|HL7

**Documentation**:
The patient's gender. There is a valueset defined based on the values in PID-8-1, but downstream consumers are free to define their own accepted values. Please refer to the consumer-specific schema if you have questions.

---

**Name**: PtID

**ReportStream Internal Name**: patient_id

**Type**: TEXT

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:
The ID for the patient within one of the reporting entities for this lab result. It could be the
the patient ID from the testing lab, the oder placer, the ordering provider, or even within the PRIME system itself.

---

**Name**: PtLastName

**ReportStream Internal Name**: patient_last_name

**Type**: PERSON_NAME

**PII**: Yes

**Cardinality**: [1..1]

**Documentation**:
The patient's last name
---

**Name**: PtPhone

**ReportStream Internal Name**: patient_phone_number

**Type**: TELEPHONE

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:
The patient's phone number with area code
---

**Name**: PtRace

**ReportStream Internal Name**: patient_race

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
1002-5|American Indian or Alaska Native|HL7
2028-9|Asian|HL7
2054-5|Black or African American|HL7
2076-8|Native Hawaiian or Other Pacific Islander|HL7
2106-3|White|HL7
2131-1|Other|HL7
UNK|Unknown|NULLFL
ASKU|Asked, but unknown|NULLFL
1002-5|American Indian or Alaska Native|HL7
2028-9|Asian|HL7
2054-5|Black or African American|HL7
2076-8|Native Hawaiian or Other Pacific Islander|HL7
2106-3|White|HL7
2131-1|Other|HL7
UNK|Unknown|NULLFL
ASKU|Asked, but unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
1002-5|AI|HL7
2028-9|A|HL7
2054-5|B|HL7
2076-8|PI|HL7
2106-3|W|HL7
2131-1|Other|HL7
UNK|U|HL7
ASKU|U|HL7

**Documentation**:
The patient's race. There is a common valueset defined for race values, but some states may choose to define different code/value combinations.

---

**Name**: PtAddrState

**ReportStream Internal Name**: patient_state

**Type**: TABLE

**PII**: No

**Cardinality**: [1..1]

**Table**: fips-county

**Table Column**: State

**Documentation**:
The patient's state
---

**Name**: PtAddrStreet

**ReportStream Internal Name**: patient_street

**Type**: STREET

**PII**: Yes

**Cardinality**: [0..1]

**Documentation**:
The patient's street address
---

**Name**: PtTribe

**ReportStream Internal Name**: patient_tribal_citizenship

**Type**: CODE

**PII**: No

**Format**: use value found in the Code column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
338|Village of Afognak|HL7
339|Agdaagux Tribe of King Cove|HL7
340|Native Village of Akhiok|HL7
341|Akiachak Native Community|HL7
342|Akiak Native Community|HL7
343|Native Village of Akutan|HL7
344|Village of Alakanuk|HL7
345|Alatna Village|HL7
346|Native Village of Aleknagik|HL7
347|Algaaciq Native Village (St. Mary's)|HL7
348|Allakaket Village|HL7
349|Native Village of Ambler|HL7
350|Village of Anaktuvuk Pass|HL7
351|Yupiit of Andreafski|HL7
352|Angoon Community Association|HL7
353|Village of Aniak|HL7
354|Anvik Village|HL7
355|Arctic Village (See Native Village of Venetie Trib|HL7
356|Asa carsarmiut Tribe (formerly Native Village of M|HL7
357|Native Village of Atka|HL7
358|Village of Atmautluak|HL7
359|Atqasuk Village (Atkasook)|HL7
360|Native Village of Barrow Inupiat Traditional Gover|HL7
361|Beaver Village|HL7
362|Native Village of Belkofski|HL7
363|Village of Bill Moore's Slough|HL7
364|Birch Creek Tribe|HL7
365|Native Village of Brevig Mission|HL7
366|Native Village of Buckland|HL7
367|Native Village of Cantwell|HL7
368|Native Village of Chanega (aka Chenega)|HL7
369|Chalkyitsik Village|HL7
370|Village of Chefornak|HL7
371|Chevak Native Village|HL7
372|Chickaloon Native Village|HL7
373|Native Village of Chignik|HL7
374|Native Village of Chignik Lagoon|HL7
375|Chignik Lake Village|HL7
376|Chilkat Indian Village (Klukwan)|HL7
377|Chilkoot Indian Association (Haines)|HL7
378|Chinik Eskimo Community (Golovin)|HL7
379|Native Village of Chistochina|HL7
380|Native Village of Chitina|HL7
381|Native Village of Chuathbaluk (Russian Mission, Ku|HL7
382|Chuloonawick Native Village|HL7
383|Circle Native Community|HL7
384|Village of Clark's Point|HL7
385|Native Village of Council|HL7
386|Craig Community Association|HL7
387|Village of Crooked Creek|HL7
388|Curyung Tribal Council (formerly Native Village of|HL7
389|Native Village of Deering|HL7
390|Native Village of Diomede (aka Inalik)|HL7
391|Village of Dot Lake|HL7
392|Douglas Indian Association|HL7
393|Native Village of Eagle|HL7
394|Native Village of Eek|HL7
395|Egegik Village|HL7
396|Eklutna Native Village|HL7
397|Native Village of Ekuk|HL7
398|Ekwok Village|HL7
399|Native Village of Elim|HL7
400|Emmonak Village|HL7
401|Evansville Village (aka Bettles Field)|HL7
402|Native Village of Eyak (Cordova)|HL7
403|Native Village of False Pass|HL7
404|Native Village of Fort Yukon|HL7
405|Native Village of Gakona|HL7
406|Galena Village (aka Louden Village)|HL7
407|Native Village of Gambell|HL7
408|Native Village of Georgetown|HL7
409|Native Village of Goodnews Bay|HL7
410|Organized Village of Grayling (aka Holikachuk)|HL7
411|Gulkana Village|HL7
412|Native Village of Hamilton|HL7
413|Healy Lake Village|HL7
414|Holy Cross Village|HL7
415|Hoonah Indian Association|HL7
416|Native Village of Hooper Bay|HL7
417|Hughes Village|HL7
418|Huslia Village|HL7
419|Hydaburg Cooperative Association|HL7
420|Igiugig Village|HL7
421|Village of Iliamna|HL7
422|Inupiat Community of the Arctic Slope|HL7
423|Iqurmuit Traditional Council (formerly Native Vill|HL7
424|Ivanoff Bay Village|HL7
425|Kaguyak Village|HL7
426|Organized Village of Kake|HL7
427|Kaktovik Village (aka Barter Island)|HL7
428|Village of Kalskag|HL7
429|Village of Kaltag|HL7
430|Native Village of Kanatak|HL7
431|Native Village of Karluk|HL7
432|Organized Village of Kasaan|HL7
433|Native Village of Kasigluk|HL7
434|Kenaitze Indian Tribe|HL7
435|Ketchikan Indian Corporation|HL7
436|Native Village of Kiana|HL7
437|King Island Native Community|HL7
438|King Salmon Tribe|HL7
439|Native Village of Kipnuk|HL7
440|Native Village of Kivalina|HL7
441|Klawock Cooperative Association|HL7
442|Native Village of Kluti Kaah (aka Copper Center)|HL7
443|Knik Tribe|HL7
444|Native Village of Kobuk|HL7
445|Kokhanok Village|HL7
446|Native Village of Kongiganak|HL7
447|Village of Kotlik|HL7
448|Native Village of Kotzebue|HL7
449|Native Village of Koyuk|HL7
450|Koyukuk Native Village|HL7
451|Organized Village of Kwethluk|HL7
452|Native Village of Kwigillingok|HL7
453|Native Village of Kwinhagak (aka Quinhagak)|HL7
454|Native Village of Larsen Bay|HL7
455|Levelock Village|HL7
456|Lesnoi Village (aka Woody Island)|HL7
457|Lime Village|HL7
458|Village of Lower Kalskag|HL7
459|Manley Hot Springs Village|HL7
460|Manokotak Village|HL7
461|Native Village of Marshall (aka Fortuna Ledge)|HL7
462|Native Village of Mary's Igloo|HL7
463|McGrath Native Village|HL7
464|Native Village of Mekoryuk|HL7
465|Mentasta Traditional Council|HL7
466|Metlakatla Indian Community, Annette Island Reserv|HL7
467|Native Village of Minto|HL7
468|Naknek Native Village|HL7
469|Native Village of Nanwalek (aka English Bay)|HL7
470|Native Village of Napaimute|HL7
471|Native Village of Napakiak|HL7
472|Native Village of Napaskiak|HL7
473|Native Village of Nelson Lagoon|HL7
474|Nenana Native Association|HL7
475|New Koliganek Village Council (formerly Koliganek|HL7
476|New Stuyahok Village|HL7
477|Newhalen Village|HL7
478|Newtok Village|HL7
479|Native Village of Nightmute|HL7
480|Nikolai Village|HL7
481|Native Village of Nikolski|HL7
482|Ninilchik Village|HL7
483|Native Village of Noatak|HL7
484|Nome Eskimo Community|HL7
485|Nondalton Village|HL7
486|Noorvik Native Community|HL7
487|Northway Village|HL7
488|Native Village of Nuiqsut (aka Nooiksut)|HL7
489|Nulato Village|HL7
490|Nunakauyarmiut Tribe (formerly Native Village of T|HL7
491|Native Village of Nunapitchuk|HL7
492|Village of Ohogamiut|HL7
493|Village of Old Harbor|HL7
494|Orutsararmuit Native Village (aka Bethel)|HL7
495|Oscarville Traditional Village|HL7
496|Native Village of Ouzinkie|HL7
497|Native Village of Paimiut|HL7
498|Pauloff Harbor Village|HL7
499|Pedro Bay Village|HL7
500|Native Village of Perryville|HL7
501|Petersburg Indian Association|HL7
502|Native Village of Pilot Point|HL7
503|Pilot Station Traditional Village|HL7
504|Native Village of Pitka's Point|HL7
505|Platinum Traditional Village|HL7
506|Native Village of Point Hope|HL7
507|Native Village of Point Lay|HL7
508|Native Village of Port Graham|HL7
509|Native Village of Port Heiden|HL7
510|Native Village of Port Lions|HL7
511|Portage Creek Village (aka Ohgsenakale)|HL7
512|Pribilof Islands Aleut Communities of St. Paul & S|HL7
513|Qagan Tayagungin Tribe of Sand Point Village|HL7
514|Qawalangin Tribe of Unalaska|HL7
515|Rampart Village|HL7
516|Village of Red Devil|HL7
517|Native Village of Ruby|HL7
518|Saint George Island(See Pribilof Islands Aleut Com|HL7
519|Native Village of Saint Michael|HL7
520|Saint Paul Island (See Pribilof Islands Aleut Comm|HL7
521|Village of Salamatoff|HL7
522|Native Village of Savoonga|HL7
523|Organized Village of Saxman|HL7
524|Native Village of Scammon Bay|HL7
525|Native Village of Selawik|HL7
526|Seldovia Village Tribe|HL7
527|Shageluk Native Village|HL7
528|Native Village of Shaktoolik|HL7
529|Native Village of Sheldon's Point|HL7
530|Native Village of Shishmaref|HL7
531|Shoonaq Tribe of Kodiak|HL7
532|Native Village of Shungnak|HL7
533|Sitka Tribe of Alaska|HL7
534|Skagway Village|HL7
535|Village of Sleetmute|HL7
536|Village of Solomon|HL7
537|South Naknek Village|HL7
538|Stebbins Community Association|HL7
539|Native Village of Stevens|HL7
540|Village of Stony River|HL7
541|Takotna Village|HL7
542|Native Village of Tanacross|HL7
543|Native Village of Tanana|HL7
544|Native Village of Tatitlek|HL7
545|Native Village of Tazlina|HL7
546|Telida Village|HL7
547|Native Village of Teller|HL7
548|Native Village of Tetlin|HL7
549|Central Council of the Tlingit and Haida Indian Tb|HL7
550|Traditional Village of Togiak|HL7
551|Tuluksak Native Community|HL7
552|Native Village of Tuntutuliak|HL7
553|Native Village of Tununak|HL7
554|Twin Hills Village|HL7
555|Native Village of Tyonek|HL7
556|Ugashik Village|HL7
557|Umkumiute Native Village|HL7
558|Native Village of Unalakleet|HL7
559|Native Village of Unga|HL7
560|Village of Venetie (See Native Village of Venetie|HL7
561|Native Village of Venetie Tribal Government (Arcti|HL7
562|Village of Wainwright|HL7
563|Native Village of Wales|HL7
564|Native Village of White Mountain|HL7
565|Wrangell Cooperative Association|HL7
566|Yakutat Tlingit Tribe|HL7
1|Absentee-Shawnee Tribe of Indians of Oklahoma|HL7
10|Assiniboine and Sioux Tribes of the Fort Peck Indi|HL7
100|Havasupai Tribe of the Havasupai Reservation, Ariz|HL7
101|Ho-Chunk Nation of Wisconsin (formerly known as th|HL7
102|Hoh Indian Tribe of the Hoh Indian Reservation, Wa|HL7
103|Hoopa Valley Tribe, California|HL7
104|Hopi Tribe of Arizona|HL7
105|Hopland Band of Pomo Indians of the Hopland Ranche|HL7
106|Houlton Band of Maliseet Indians of Maine|HL7
107|Hualapai Indian Tribe of the Hualapai Indian Reser|HL7
108|Huron Potawatomi, Inc., Michigan|HL7
109|Inaja Band of Diegueno Mission Indians of the Inaj|HL7
11|Augustine Band of Cahuilla Mission Indians of the|HL7
110|Ione Band of Miwok Indians of California|HL7
111|Iowa Tribe of Kansas and Nebraska|HL7
112|Iowa Tribe of Oklahoma|HL7
113|Jackson Rancheria of Me-Wuk Indians of California|HL7
114|Jamestown S'Klallam Tribe of Washington|HL7
115|Jamul Indian Village of California|HL7
116|Jena Band of Choctaw Indians, Louisiana|HL7
117|Jicarilla Apache Tribe of the Jicarilla Apache Ind|HL7
118|Kaibab Band of Paiute Indians of the Kaibab Indian|HL7
119|Kalispel Indian Community of the Kalispel Reservat|HL7
12|Bad River Band of the Lake Superior Tribe of Chipp|HL7
120|Karuk Tribe of California|HL7
121|Kashia Band of Pomo Indians of the Stewarts Point|HL7
122|Kaw Nation, Oklahoma|HL7
123|Keweenaw Bay Indian Community of L'Anse and Ontona|HL7
124|Kialegee Tribal Town, Oklahoma|HL7
125|Kickapoo Tribe of Indians of the Kickapoo Reservat|HL7
126|Kickapoo Tribe of Oklahoma|HL7
127|Kickapoo Traditional Tribe of Texas|HL7
128|Kiowa Indian Tribe of Oklahoma|HL7
129|Klamath Indian Tribe of Oregon|HL7
13|Bay Mills Indian Community of the Sault Ste. Marie|HL7
130|Kootenai Tribe of Idaho|HL7
131|La Jolla Band of Luiseno Mission Indians of the La|HL7
132|La Posta Band of Diegueno Mission Indians of the L|HL7
133|Lac Courte Oreilles Band of Lake Superior Chippewa|HL7
134|Lac du Flambeau Band of Lake Superior Chippewa Ind|HL7
135|Lac Vieux Desert Band of Lake Superior Chippewa In|HL7
136|Las Vegas Tribe of Paiute Indians of the Las Vegas|HL7
137|Little River Band of Ottawa Indians of Michigan|HL7
138|Little Traverse Bay Bands of Odawa Indians of Mich|HL7
139|Lower Lake Rancheria, California|HL7
14|Bear River Band of the Rohnerville Rancheria, Cali|HL7
140|Los Coyotes Band of Cahuilla Mission Indians of th|HL7
141|Lovelock Paiute Tribe of the Lovelock Indian Colon|HL7
142|Lower Brule Sioux Tribe of the Lower Brule Reserva|HL7
143|Lower Elwha Tribal Community of the Lower Elwha Re|HL7
144|Lower Sioux Indian Community of Minnesota Mdewakan|HL7
145|Lummi Tribe of the Lummi Reservation, Washington|HL7
146|Lytton Rancheria of California|HL7
147|Makah Indian Tribe of the Makah Indian Reservation|HL7
148|Manchester Band of Pomo Indians of the Manchester-|HL7
149|Manzanita Band of Diegueno Mission Indians of the|HL7
15|Berry Creek Rancheria of Maidu Indians of Californ|HL7
150|Mashantucket Pequot Tribe of Connecticut|HL7
151|Match-e-be-nash-she-wish Band of Pottawatomi India|HL7
152|Mechoopda Indian Tribe of Chico Rancheria, Califor|HL7
153|Menominee Indian Tribe of Wisconsin|HL7
154|Mesa Grande Band of Diegueno Mission Indians of th|HL7
155|Mescalero Apache Tribe of the Mescalero Reservatio|HL7
156|Miami Tribe of Oklahoma|HL7
157|Miccosukee Tribe of Indians of Florida|HL7
158|Middletown Rancheria of Pomo Indians of California|HL7
159|Minnesota Chippewa Tribe, Minnesota (Six component|HL7
16|Big Lagoon Rancheria, California|HL7
160|Bois Forte Band (Nett Lake); Fond du Lac Band; Gra|HL7
161|Mississippi Band of Choctaw Indians, Mississippi|HL7
162|Moapa Band of Paiute Indians of the Moapa River In|HL7
163|Modoc Tribe of Oklahoma|HL7
164|Mohegan Indian Tribe of Connecticut|HL7
165|Mooretown Rancheria of Maidu Indians of California|HL7
166|Morongo Band of Cahuilla Mission Indians of the Mo|HL7
167|Muckleshoot Indian Tribe of the Muckleshoot Reserv|HL7
168|Muscogee (Creek) Nation, Oklahoma|HL7
169|Narragansett Indian Tribe of Rhode Island|HL7
17|Big Pine Band of Owens Valley Paiute Shoshone Indi|HL7
170|Navajo Nation, Arizona, New Mexico & Utah|HL7
171|Nez Perce Tribe of Idaho|HL7
172|Nisqually Indian Tribe of the Nisqually Reservatio|HL7
173|Nooksack Indian Tribe of Washington|HL7
174|Northern Cheyenne Tribe of the Northern Cheyenne I|HL7
175|Northfork Rancheria of Mono Indians of California|HL7
176|Northwestern Band of Shoshoni Nation of Utah (Wash|HL7
177|Oglala Sioux Tribe of the Pine Ridge Reservation,|HL7
178|Omaha Tribe of Nebraska|HL7
179|Oneida Nation of New York|HL7
18|Big Sandy Rancheria of Mono Indians of California|HL7
180|Oneida Tribe of Wisconsin|HL7
181|Onondaga Nation of New York|HL7
182|Osage Tribe, Oklahoma|HL7
183|Ottawa Tribe of Oklahoma|HL7
184|Otoe-Missouria Tribe of Indians, Oklahoma|HL7
185|Paiute Indian Tribe of Utah|HL7
186|Paiute-Shoshone Indians of the Bishop Community of|HL7
187|Paiute-Shoshone Tribe of the Fallon Reservation an|HL7
188|Paiute-Shoshone Indians of the Lone Pine Community|HL7
189|Pala Band of Luiseno Mission Indians of the Pala R|HL7
19|Big Valley Band of Pomo Indians of the Big Valley|HL7
190|Pascua Yaqui Tribe of Arizona|HL7
191|Paskenta Band of Nomlaki Indians of California|HL7
192|Passamaquoddy Tribe of Maine|HL7
193|Pauma Band of Luiseno Mission Indians of the Pauma|HL7
194|Pawnee Nation of Oklahoma|HL7
195|Pechanga Band of Luiseno Mission Indians of the Pe|HL7
196|Penobscot Tribe of Maine|HL7
197|Peoria Tribe of Indians of Oklahoma|HL7
198|Picayune Rancheria of Chukchansi Indians of Califo|HL7
199|Pinoleville Rancheria of Pomo Indians of Californi|HL7
2|Agua Caliente Band of Cahuilla Indians of the Agua|HL7
20|Blackfeet Tribe of the Blackfeet Indian Reservatio|HL7
200|Pit River Tribe, California (includes Big Bend, Lo|HL7
201|Poarch Band of Creek Indians of Alabama|HL7
202|Pokagon Band of Potawatomi Indians of Michigan|HL7
203|Ponca Tribe of Indians of Oklahoma|HL7
204|Ponca Tribe of Nebraska|HL7
205|Port Gamble Indian Community of the Port Gamble Re|HL7
206|Potter Valley Rancheria of Pomo Indians of Califor|HL7
207|Prairie Band of Potawatomi Indians, Kansas|HL7
208|Prairie Island Indian Community of Minnesota Mdewa|HL7
209|Pueblo of Acoma, New Mexico|HL7
21|Blue Lake Rancheria, California|HL7
210|Pueblo of Cochiti, New Mexico|HL7
211|Pueblo of Jemez, New Mexico|HL7
212|Pueblo of Isleta, New Mexico|HL7
213|Pueblo of Laguna, New Mexico|HL7
214|Pueblo of Nambe, New Mexico|HL7
215|Pueblo of Picuris, New Mexico|HL7
216|Pueblo of Pojoaque, New Mexico|HL7
217|Pueblo of San Felipe, New Mexico|HL7
218|Pueblo of San Juan, New Mexico|HL7
219|Pueblo of San Ildefonso, New Mexico|HL7
22|Bridgeport Paiute Indian Colony of California|HL7
220|Pueblo of Sandia, New Mexico|HL7
221|Pueblo of Santa Ana, New Mexico|HL7
222|Pueblo of Santa Clara, New Mexico|HL7
223|Pueblo of Santo Domingo, New Mexico|HL7
224|Pueblo of Taos, New Mexico|HL7
225|Pueblo of Tesuque, New Mexico|HL7
226|Pueblo of Zia, New Mexico|HL7
227|Puyallup Tribe of the Puyallup Reservation, Washin|HL7
228|Pyramid Lake Paiute Tribe of the Pyramid Lake Rese|HL7
229|Quapaw Tribe of Indians, Oklahoma|HL7
23|Buena Vista Rancheria of Me-Wuk Indians of Califor|HL7
230|Quartz Valley Indian Community of the Quartz Valle|HL7
231|Quechan Tribe of the Fort Yuma Indian Reservation,|HL7
232|Quileute Tribe of the Quileute Reservation, Washin|HL7
233|Quinault Tribe of the Quinault Reservation, Washin|HL7
234|Ramona Band or Village of Cahuilla Mission Indians|HL7
235|Red Cliff Band of Lake Superior Chippewa Indians o|HL7
236|Red Lake Band of Chippewa Indians of the Red Lake|HL7
237|Redding Rancheria, California|HL7
238|Redwood Valley Rancheria of Pomo Indians of Califo|HL7
239|Reno-Sparks Indian Colony, Nevada|HL7
24|Burns Paiute Tribe of the Burns Paiute Indian Colo|HL7
240|Resighini Rancheria, California (formerly known as|HL7
241|Rincon Band of Luiseno Mission Indians of the Rinc|HL7
242|Robinson Rancheria of Pomo Indians of California|HL7
243|Rosebud Sioux Tribe of the Rosebud Indian Reservat|HL7
244|Round Valley Indian Tribes of the Round Valley Res|HL7
245|Rumsey Indian Rancheria of Wintun Indians of Calif|HL7
246|Sac and Fox Tribe of the Mississippi in Iowa|HL7
247|Sac and Fox Nation of Missouri in Kansas and Nebra|HL7
248|Sac and Fox Nation, Oklahoma|HL7
249|Saginaw Chippewa Indian Tribe of Michigan, Isabell|HL7
25|Cabazon Band of Cahuilla Mission Indians of the Ca|HL7
250|Salt River Pima-Maricopa Indian Community of the S|HL7
251|Samish Indian Tribe, Washington|HL7
252|San Carlos Apache Tribe of the San Carlos Reservat|HL7
253|San Juan Southern Paiute Tribe of Arizona|HL7
254|San Manual Band of Serrano Mission Indians of the|HL7
255|San Pasqual Band of Diegueno Mission Indians of Ca|HL7
256|Santa Rosa Indian Community of the Santa Rosa Ranc|HL7
257|Santa Rosa Band of Cahuilla Mission Indians of the|HL7
258|Santa Ynez Band of Chumash Mission Indians of the|HL7
259|Santa Ysabel Band of Diegueno Mission Indians of t|HL7
26|Cachil DeHe Band of Wintun Indians of the Colusa I|HL7
260|Santee Sioux Tribe of the Santee Reservation of Ne|HL7
261|Sauk-Suiattle Indian Tribe of Washington|HL7
262|Sault Ste. Marie Tribe of Chippewa Indians of Mich|HL7
263|Scotts Valley Band of Pomo Indians of California|HL7
264|Seminole Nation of Oklahoma|HL7
265|Seminole Tribe of Florida, Dania, Big Cypress, Bri|HL7
266|Seneca Nation of New York|HL7
267|Seneca-Cayuga Tribe of Oklahoma|HL7
268|Shakopee Mdewakanton Sioux Community of Minnesota|HL7
269|Shawnee Tribe, Oklahoma|HL7
27|Caddo Indian Tribe of Oklahoma|HL7
270|Sherwood Valley Rancheria of Pomo Indians of Calif|HL7
271|Shingle Springs Band of Miwok Indians, Shingle Spr|HL7
272|Shoalwater Bay Tribe of the Shoalwater Bay Indian|HL7
273|Shoshone Tribe of the Wind River Reservation, Wyom|HL7
274|Shoshone-Bannock Tribes of the Fort Hall Reservati|HL7
275|Shoshone-Paiute Tribes of the Duck Valley Reservat|HL7
276|Sisseton-Wahpeton Sioux Tribe of the Lake Traverse|HL7
277|Skokomish Indian Tribe of the Skokomish Reservatio|HL7
278|Skull Valley Band of Goshute Indians of Utah|HL7
279|Smith River Rancheria, California|HL7
28|Cahuilla Band of Mission Indians of the Cahuilla R|HL7
280|Snoqualmie Tribe, Washington|HL7
281|Soboba Band of Luiseno Indians, California (former|HL7
282|Sokaogon Chippewa Community of the Mole Lake Band|HL7
283|Southern Ute Indian Tribe of the Southern Ute Rese|HL7
284|Spirit Lake Tribe, North Dakota (formerly known as|HL7
285|Spokane Tribe of the Spokane Reservation, Washingt|HL7
286|Squaxin Island Tribe of the Squaxin Island Reserva|HL7
287|St. Croix Chippewa Indians of Wisconsin, St. Croix|HL7
288|St. Regis Band of Mohawk Indians of New York|HL7
289|Standing Rock Sioux Tribe of North & South Dakota|HL7
29|Cahto Indian Tribe of the Laytonville Rancheria, C|HL7
290|Stockbridge-Munsee Community of Mohican Indians of|HL7
291|Stillaguamish Tribe of Washington|HL7
292|Summit Lake Paiute Tribe of Nevada|HL7
293|Suquamish Indian Tribe of the Port Madison Reserva|HL7
294|Susanville Indian Rancheria, California|HL7
295|Swinomish Indians of the Swinomish Reservation, Wa|HL7
296|Sycuan Band of Diegueno Mission Indians of Califor|HL7
297|Table Bluff Reservation - Wiyot Tribe, California|HL7
298|Table Mountain Rancheria of California|HL7
299|Te-Moak Tribe of Western Shoshone Indians of Nevad|HL7
3|Ak Chin Indian Community of the Maricopa (Ak Chin)|HL7
30|California Valley Miwok Tribe, California (formerl|HL7
300|Thlopthlocco Tribal Town, Oklahoma|HL7
301|Three Affiliated Tribes of the Fort Berthold Reser|HL7
302|Tohono O'odham Nation of Arizona|HL7
303|Tonawanda Band of Seneca Indians of New York|HL7
304|Tonkawa Tribe of Indians of Oklahoma|HL7
305|Tonto Apache Tribe of Arizona|HL7
306|Torres-Martinez Band of Cahuilla Mission Indians o|HL7
307|Tule River Indian Tribe of the Tule River Reservat|HL7
308|Tulalip Tribes of the Tulalip Reservation, Washing|HL7
309|Tunica-Biloxi Indian Tribe of Louisiana|HL7
31|Campo Band of Diegueno Mission Indians of the Camp|HL7
310|Tuolumne Band of Me-Wuk Indians of the Tuolumne Ra|HL7
311|Turtle Mountain Band of Chippewa Indians of North|HL7
312|Tuscarora Nation of New York|HL7
313|Twenty-Nine Palms Band of Mission Indians of Calif|HL7
314|United Auburn Indian Community of the Auburn Ranch|HL7
315|United Keetoowah Band of Cherokee Indians of Oklah|HL7
316|Upper Lake Band of Pomo Indians of Upper Lake Ranc|HL7
317|Upper Sioux Indian Community of the Upper Sioux Re|HL7
318|Upper Skagit Indian Tribe of Washington|HL7
319|Ute Indian Tribe of the Uintah & Ouray Reservation|HL7
32|Capitan Grande Band of Diegueno Mission Indians of|HL7
320|Ute Mountain Tribe of the Ute Mountain Reservation|HL7
321|Utu Utu Gwaitu Paiute Tribe of the Benton Paiute R|HL7
322|Walker River Paiute Tribe of the Walker River Rese|HL7
323|Wampanoag Tribe of Gay Head (Aquinnah) of Massachu|HL7
324|Washoe Tribe of Nevada & California (Carson Colony|HL7
325|White Mountain Apache Tribe of the Fort Apache Res|HL7
326|Wichita and Affiliated Tribes (Wichita, Keechi, Wa|HL7
327|Winnebago Tribe of Nebraska|HL7
328|Winnemucca Indian Colony of Nevada|HL7
329|Wyandotte Tribe of Oklahoma|HL7
33|Barona Group of Capitan Grande Band of Mission Ind|HL7
330|Yankton Sioux Tribe of South Dakota|HL7
331|Yavapai-Apache Nation of the Camp Verde Indian Res|HL7
332|Yavapai-Prescott Tribe of the Yavapai Reservation,|HL7
333|Yerington Paiute Tribe of the Yerington Colony & C|HL7
334|Yomba Shoshone Tribe of the Yomba Reservation, Nev|HL7
335|Ysleta Del Sur Pueblo of Texas|HL7
336|Yurok Tribe of the Yurok Reservation, California|HL7
337|Zuni Tribe of the Zuni Reservation, New Mexico|HL7
34|Viejas (Baron Long) Group of Capitan Grande Band o|HL7
35|Catawba Indian Nation (aka Catawba Tribe of South|HL7
36|Cayuga Nation of New York|HL7
37|Cedarville Rancheria, California|HL7
38|Chemehuevi Indian Tribe of the Chemehuevi Reservat|HL7
39|Cher-Ae Heights Indian Community of the Trinidad R|HL7
4|Alabama-Coushatta Tribes of Texas|HL7
40|Cherokee Nation, Oklahoma|HL7
41|Cheyenne-Arapaho Tribes of Oklahoma|HL7
42|Cheyenne River Sioux Tribe of the Cheyenne River|HL7
43|Chickasaw Nation, Oklahoma|HL7
44|Chicken Ranch Rancheria of Me-Wuk Indians of Calif|HL7
45|Chippewa-Cree Indians of the Rocky Boy's Reservati|HL7
46|Chitimacha Tribe of Louisiana|HL7
47|Choctaw Nation of Oklahoma|HL7
48|Citizen Potawatomi Nation, Oklahoma|HL7
49|Cloverdale Rancheria of Pomo Indians of California|HL7
5|Alabama-Quassarte Tribal Town, Oklahoma|HL7
50|Cocopah Tribe of Arizona|HL7
51|Coeur D'Alene Tribe of the Coeur D'Alene Reservati|HL7
52|Cold Springs Rancheria of Mono Indians of Californ|HL7
53|Colorado River Indian Tribes of the Colorado River|HL7
54|Comanche Indian Tribe, Oklahoma|HL7
55|Confederated Salish & Kootenai Tribes of the Flath|HL7
56|Confederated Tribes of the Chehalis Reservation, W|HL7
57|Confederated Tribes of the Colville Reservation, W|HL7
58|Confederated Tribes of the Coos, Lower Umpqua and|HL7
59|Confederated Tribes of the Goshute Reservation, Ne|HL7
6|Alturas Indian Rancheria, California|HL7
60|Confederated Tribes of the Grand Ronde Community o|HL7
61|Confederated Tribes of the Siletz Reservation, Ore|HL7
62|Confederated Tribes of the Umatilla Reservation, O|HL7
63|Confederated Tribes of the Warm Springs Reservatio|HL7
64|Confederated Tribes and Bands of the Yakama Indian|HL7
65|Coquille Tribe of Oregon|HL7
66|Cortina Indian Rancheria of Wintun Indians of Cali|HL7
67|Coushatta Tribe of Louisiana|HL7
68|Cow Creek Band of Umpqua Indians of Oregon|HL7
69|Coyote Valley Band of Pomo Indians of California|HL7
7|Apache Tribe of Oklahoma|HL7
70|Crow Tribe of Montana|HL7
71|Crow Creek Sioux Tribe of the Crow Creek Reservati|HL7
72|Cuyapaipe Community of Diegueno Mission Indians of|HL7
73|Death Valley Timbi-Sha Shoshone Band of California|HL7
74|Delaware Nation, Oklahoma (formerly Delaware Tribe|HL7
75|Delaware Tribe of Indians, Oklahoma|HL7
76|Dry Creek Rancheria of Pomo Indians of California|HL7
77|Duckwater Shoshone Tribe of the Duckwater Reservat|HL7
78|Eastern Band of Cherokee Indians of North Carolina|HL7
79|Eastern Shawnee Tribe of Oklahoma|HL7
8|Arapahoe Tribe of the Wind River Reservation, Wyom|HL7
80|Elem Indian Colony of Pomo Indians of the Sulphur|HL7
81|Elk Valley Rancheria, California|HL7
82|Ely Shoshone Tribe of Nevada|HL7
83|Enterprise Rancheria of Maidu Indians of Californi|HL7
84|Flandreau Santee Sioux Tribe of South Dakota|HL7
85|Forest County Potawatomi Community of Wisconsin Po|HL7
86|Fort Belknap Indian Community of the Fort Belknap|HL7
87|Fort Bidwell Indian Community of the Fort Bidwell|HL7
88|Fort Independence Indian Community of Paiute India|HL7
89|Fort McDermitt Paiute and Shoshone Tribes of the F|HL7
9|Aroostook Band of Micmac Indians of Maine|HL7
90|Fort McDowell Yavapai Nation, Arizona (formerly th|HL7
91|Fort Mojave Indian Tribe of Arizona, California|HL7
92|Fort Sill Apache Tribe of Oklahoma|HL7
93|Gila River Indian Community of the Gila River Indi|HL7
94|Grand Traverse Band of Ottawa & Chippewa Indians o|HL7
95|Graton Rancheria, California|HL7
96|Greenville Rancheria of Maidu Indians of Californi|HL7
97|Grindstone Indian Rancheria of Wintun-Wailaki Indi|HL7
98|Guidiville Rancheria of California|HL7
99|Hannahville Indian Community of Wisconsin Potawato|HL7

**Documentation**:
The tribal citizenship of the patient using the TribalEntityUS (OID 2.16.840.1.113883.5.140) table
---

**Name**: PtAddrZip

**ReportStream Internal Name**: patient_zip_code

**Type**: POSTAL_CODE

**PII**: No

**Cardinality**: [0..1]

**Documentation**:
The patient's zip code
---

**Name**: Pregnant

**ReportStream Internal Name**: pregnant

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 82810-3

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
77386006|Pregnant|SNOMED_CT
60001007|Not Pregnant|SNOMED_CT
261665006|Unknown|SNOMED_CT
77386006|Pregnant|SNOMED_CT
60001007|Not Pregnant|SNOMED_CT
261665006|Unknown|SNOMED_CT

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
77386006|Y|SNOMED_CT
60001007|N|SNOMED_CT
261665006|U|SNOMED_CT

**Documentation**:
Is the patient pregnant?
---

**Name**: RptFacName

**ReportStream Internal Name**: reporting_facility

**Type**: HD

**PII**: No

**Default Value**: CDC PRIME

**Cardinality**: [0..1]

**Documentation**:
The reporting facility for the message, as specified by the receiver. This is typically used if PRIME is the
aggregator

---

**Name**: SenderCLIA

**ReportStream Internal Name**: reporting_facility_clia

**Type**: ID_CLIA

**PII**: No

**HL7 Fields**

- [MSH-4-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/MSH.4.2)
- [PID-3-4-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.4.2)
- [PID-3-6-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/PID.3.6.2)
- [SPM-2-1-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.1.3)
- [SPM-2-2-3](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2.2.3)

**Cardinality**: [0..1]

**Documentation**:
The reporting facility's CLIA
---

**Name**: CongSetting

**ReportStream Internal Name**: resident_congregate_setting

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 95421-4

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Does the patient reside in a congregate care setting?
---

**Name**: SendingApp

**ReportStream Internal Name**: sending_application

**Type**: HD

**PII**: No

**Default Value**: CDC PRIME - Atlanta, Georgia (Dekalb)^2.16.840.1.114222.4.1.237821^ISO

**Cardinality**: [0..1]

**Documentation**:
The name and OID for the application sending information to the receivers

---

**Name**: SpecCollDt

**ReportStream Internal Name**: specimen_collection_date_time

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**HL7 Fields**

- [OBR-7](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.7)
- [OBR-8](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBR.8)
- [OBX-14](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/OBX.14)
- [SPM-17-1](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.17.1)

**Cardinality**: [0..1]

**Documentation**:
The date which the specimen was collected. The default format is yyyyMMddHHmmsszz

---

**Name**: AccessionNum

**ReportStream Internal Name**: specimen_id

**Type**: EI

**PII**: No

**HL7 Fields**

- [SPM-2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2)

**Cardinality**: [0..1]


**Reference URL**:
[https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2](https://hl7-definition.caristix.com/v2/HL7v2.5.1/Fields/SPM.2) 
                
**Documentation**:
A unique code for this specimen
---

**Name**: SpecType

**ReportStream Internal Name**: specimen_type

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
445297001|Swab of internal nose|SNOMED_CT
258500001|Nasopharyngeal swab|SNOMED_CT
871810001|Mid-turbinate nasal swab|SNOMED_CT
697989009|Anterior nares swab|SNOMED_CT
258411007|Nasopharyngeal aspirate|SNOMED_CT
429931000124105|Nasal aspirate|SNOMED_CT
258529004|Throat swab|SNOMED_CT
119334006|Sputum specimen|SNOMED_CT
119342007|Saliva specimen|SNOMED_CT
258560004|Oral saliva sample|SNOMED_CT
258607008|Bronchoalveolar lavage fluid sample|SNOMED_CT
119364003|Serum specimen|SNOMED_CT
119361006|Plasma specimen|SNOMED_CT
440500007|Dried blood spot specimen|SNOMED_CT
258580003|Whole blood sample|SNOMED_CT
122555007|Venous blood specimen|SNOMED_CT
119297000|Blood specimen|SNOMED_CT
122554006|Capillary blood specimen|SNOMED_CT
258467004|Nasopharyngeal washings|SNOMED_CT
418932006|Oral swab specimen|SNOMED_CT
433801000124107|Nasopharyngeal and oropharyngeal swab|SNOMED_CT
309171007|Lower respiratory fluid sample|SNOMED_CT
433871000124101|Nasal washings|SNOMED_CT
441620008|Sputum specimen obtained by sputum induction|SNOMED_CT
441903006|Coughed sputum specimen|SNOMED_CT
119336008|Specimen from trachea obtained by aspiration|SNOMED_CT
258610001|Oral fluid specimen|SNOMED_CT
119335007|Specimen obtained by bronchial aspiration|SNOMED_CT
1293160008|Swab from middle region of inferior nasal meatus|SNOMED_CT
445447003|Exhaled air specimen|SNOMED_CT

**Documentation**:
The specimen source, such as Blood or Serum
---

**Name**: Symptomatic

**ReportStream Internal Name**: symptomatic_for_disease

**Type**: CODE

**PII**: No

**Format**: use value found in the Display column

**LOINC Code**: 95419-8

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL
Y|Yes|HL7
N|No|HL7
UNK|Unknown|NULLFL

**Alt Value Sets**

Code | Display | System
---- | ------- | ------
Y|Y|HL7
N|N|HL7
UNK|U|HL7

**Documentation**:
Is the patient symptomatic?
---

**Name**: ResultTestCode

**ReportStream Internal Name**: test_result

**Type**: CODE

**PII**: No

**Format**: use value found in the Code column

**Cardinality**: [0..1]

**Value Sets**

Code | Display | System
---- | ------- | ------
260373001|Detected|SNOMED_CT
260415000|Not detected|SNOMED_CT
720735008|Presumptive positive|SNOMED_CT
10828004|Positive|SNOMED_CT
42425007|Equivocal|SNOMED_CT
260385009|Negative|SNOMED_CT
895231008|Not detected in pooled specimen|SNOMED_CT
462371000124108|Detected in pooled specimen|SNOMED_CT
419984006|Inconclusive|SNOMED_CT
125154007|Specimen unsatisfactory for evaluation|SNOMED_CT
455371000124106|Invalid result|SNOMED_CT
840539006|Disease caused by sever acute respiratory syndrome coronavirus 2 (disorder)|SNOMED_CT
840544004|Suspected disease caused by severe acute respiratory coronavirus 2 (situation)|SNOMED_CT
840546002|Exposure to severe acute respiratory syndrome coronavirus 2 (event)|SNOMED_CT
840533007|Severe acute respiratory syndrome coronavirus 2 (organism)|SNOMED_CT
840536004|Antigen of severe acute respiratory syndrome coronavirus 2 (substance)|SNOMED_CT
840535000|Antibody to severe acute respiratory syndrome coronavirus 2 (substance)|SNOMED_CT
840534001|Severe acute respiratory syndrome coronavirus 2 vaccination (procedure)|SNOMED_CT
373121007|Test not done|SNOMED_CT
82334004|Indeterminate|SNOMED_CT

**Documentation**:
The result of the test performed. For IgG, IgM and CT results that give a numeric value put that here.
---

**Name**: TestResultDt

**ReportStream Internal Name**: test_result_date

**Type**: DATETIME

**PII**: No

**Format**: yyyyMMdd

**Cardinality**: [0..1]

---

**Name**: equipment_model_name

**ReportStream Internal Name**: equipment_model_name

**Type**: TABLE

**PII**: No

**Cardinality**: [0..1]


**Reference URL**:
[https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification](https://confluence.hl7.org/display/OO/Proposed+HHS+ELR+Submission+Guidance+using+HL7+v2+Messages#ProposedHHSELRSubmissionGuidanceusingHL7v2Messages-DeviceIdentification) 
                
**Table**: LIVD-SARS-CoV-2

**Table Column**: Model

---

**Name**: local_symptom_onset_date

**ReportStream Internal Name**: local_symptom_onset_date

**Type**: TEXT

**PII**: No

**Cardinality**: [0..1]

---
