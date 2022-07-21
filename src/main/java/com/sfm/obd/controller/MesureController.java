package com.sfm.obd.controller;

import com.sfm.obd.dao.ValeurParametreDao;
import com.sfm.obd.dto.ApiResponse;
import com.sfm.obd.dto.RequestMesure;
import com.sfm.obd.dto.mesures.MesureDTO;
import com.sfm.obd.enumer.PairingBox;
import com.sfm.obd.enumer.TypeAlarme;
import com.sfm.obd.model.*;
import com.sfm.obd.service.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/mesure")
public class MesureController {

    @Autowired
    private MesureService mesureService;

    @Autowired
    private MesureBrutService mesureBrutService;

    @Autowired
    private IndicateurService indicateurService;

    @Autowired
    private TypeEtatService typeEtatService;

    @Autowired
    private ValeurParametreDao valeurParametreDao;


    @Autowired
    private BoitierService boitierService;

    @Autowired
    private ErreurService erreurService;

    @Value("${indic.KeepAlive}")
    private long indicKeepAlive;

    @Value("${indic.VersionHard}")
    private long indicVersionHardware;

    @Value("${indic.VersionSoft}")
    private long indicVersionSoftWare;


    @RequestMapping(value = "/adjust", method = RequestMethod.GET)
    public void adjust() {
        List<Mesure> mesures = mesureService.findAll();
        for (Mesure mesure : mesures) {
            mesure.setDatems(mesure.getDate().getTime());
            mesureService.save(mesure);
        }
    }


    @RequestMapping(value = "/obd/save", method = RequestMethod.GET)
    public ApiResponse saveMesure(
            @RequestParam(name = "mesure", required = true) String mesureString
    ) {

        System.out.println("===== Mesure : " + mesureString);
        mesureBrutService.save(new MesureBrut(new Date(), mesureString));
        Mesure mm = new Mesure();

        /************************ Les paramètres sur lesquels on fera des traitements spécifiques ***********************/
        List<MesureDTO> mesureDTOs = new ArrayList<>();
        String versionHardware = "";
        String versionSoftWare = "";
        int keepAlive = 0;


        String[] parties = mesureString.split(";");


        /************************ Collecte de l'imei ***********************/
        String imei = parties[0];
        System.out.println("====== imei : " + imei);

        /************************ Collecte des mesures ***********************/
        for (int i = 1; i < parties.length; i++) {


            System.out.println("--------->" + parties[i]);
            String[] indicateurParams = parties[i].split(",");
            //System.out.println("--------->///" + indicateurParams[0]);
            MesureDTO mesureDTO = new MesureDTO();

            if (indicateurParams.length > 3) {
                mesureDTO.setVal1(indicateurParams[0]);
                mesureDTO.setVal2(indicateurParams[1]);
                mesureDTO.setVal3(indicateurParams[2]);
                mesureDTO.setVal4(indicateurParams[3]);
                mesureDTO.setVal5(indicateurParams[4]);
                mesureDTO.setIndicateur(Long.parseLong(indicateurParams[5]));
            } else if (indicateurParams.length == 2) {
                mesureDTO.setVal1(indicateurParams[0]);
                mesureDTO.setIndicateur(Long.parseLong(indicateurParams[1]));
            } else if (indicateurParams.length == 3) {
                mesureDTO.setVal1(indicateurParams[0]);
                mesureDTO.setVal2(indicateurParams[1]);
                mesureDTO.setIndicateur(Long.parseLong(indicateurParams[2]));
            }
            // Récupération du KeepAlive
            if (mesureDTO.getIndicateur() == indicKeepAlive) {
                keepAlive = Integer.parseInt(mesureDTO.getVal1());
                continue; // ?????? enregistrer dans la BD ou pas ?
            }
            // Récupération de la verson HardWare
            if (mesureDTO.getIndicateur() == indicVersionHardware) {
                versionHardware = mesureDTO.getVal1();
                continue;
            }
            // Récupération de la verson SoftWare
            if (mesureDTO.getIndicateur() == indicVersionSoftWare) {
                versionSoftWare = mesureDTO.getVal1();
                continue;
            }
            mesureDTOs.add(mesureDTO);
        }

        // Vérifier si le boitier est enregistré à travers l'imei
        Boitier boitier = boitierService.findByImei(imei);
        if (boitier == null) {
            // Enregistrer un nouveau boitier
            boitier = new Boitier(imei, "", new Date(), false, PairingBox.Manufactured, versionSoftWare);
            String proj = "OBD";
            boitierService.newBoitier(boitier, versionHardware, proj);
        } else {
            // Enregistrement de la mesure
            System.out.println(" Le boitier existe !!!!!!!");

            //Vérifier le KEEPAlive
            if (keepAlive == 1) {
                //	Si 1 : pas de changement d'état ==> pas d'envoi ==> pas d'enregistrement de mesure
                //		Mise à jour date de dernière connexion boitier
                //		Mise à jour version soft
                boitier.setDerniereConnection(new Date());
                boitier.setVersionSoft(versionSoftWare); // ????
                boitier = boitierService.saveBoitier(boitier);

            } else {
                //	Si 0 : Nouvelle mesure
                //		Mise à jour date de dernière connexion boitier
                boitier.setDerniereConnection(new Date());
                boitier.setVersionSoft(versionSoftWare); // ????
                boitier = boitierService.saveBoitier(boitier);
                // Enregistrer les mesures
                if (!mesureDTOs.isEmpty())
                    for (MesureDTO mes : mesureDTOs) {
                        try {
                            final Mesure mesure = new Mesure(
                                    new Date(),
                                    mes.getVal1(),
                                    mes.getVal2(),
                                    mes.getVal3(),
                                    mes.getVal4(),
                                    mes.getVal5(),
                                    boitier,
                                    indicateurService.findById(mes.getIndicateur()));
                            mesure.setDatems(mesure.getDate().getTime());
                            if (mesure.getIndicateur().getId() == 10) {

                                JSONParser parser = new JSONParser();
                                List<String> errors = new ArrayList<>();
                                errors.add(mesure.getVal1());
                                errors.add(mesure.getVal2());
                                errors.add(mesure.getVal3());
                                errors.add(mesure.getVal4());
                                errors.add(mesure.getVal5());
                                mesureService.save(mesure);

                                try (Reader reader = new FileReader("src/main/resources/Code_Error.json")) { //

                                    JSONObject jsonObject = (JSONObject) parser.parse(reader);
                                    List<String> errorString = new ArrayList<>();

                                    errors.stream().forEach(error -> {
                                        if(String.valueOf(jsonObject.get(error)) != "null"){
                                            erreurService.save(new Erreur(error, String.valueOf(jsonObject.get(error)), mesure));
                                        }

                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }
                            if(mesure.getIndicateur().getId() == 5){
                                String hex = mesure.getVal1()+mesure.getVal2()+mesure.getVal3()+mesure.getVal4()+mesure.getVal5();
                                System.out.println("ssssssssss :"+ hex);
                                if(hex.length()%2!=0){
                                    System.err.println("Invlid hex string.");
                                }

                                StringBuilder builder = new StringBuilder();

                                for (int i = 0; i < hex.length(); i = i + 2) {
                                    // Step-1 Split the hex string into two character group
                                    String s = hex.substring(i, i + 2);
                                    // Step-2 Convert the each character group into integer using valueOf method
                                    int n = Integer.valueOf(s, 16);
                                    // Step-3 Cast the integer value to char
                                    builder.append((char)n);
                                }
                                mesure.setVal1(builder.toString());
                                mesure.setVal2(null);
                                mesure.setVal3(null);
                                mesure.setVal4(null);
                                mesure.setVal5(null);
                                System.out.println("VIN = " + builder.toString());
                            }

                            if(mesure.getIndicateur().getId() == 9){
                               String DistVidange = String.valueOf(10000 + Integer.parseInt(mesure.getVal1())) ;
                                System.out.println("ggggggggg   "+mesure.getVal1());
                                System.out.println("ggggggggg   "+Integer.parseInt(mesure.getVal1()));
                                System.out.println("ggggggggg   "+DistVidange);
                               mesure.setVal1(DistVidange);
                            }

                            mesureService.save(mesure);

                            //List<ValeurParametre> valeurParametres = valeurParametreDao.findByBoitier(mesure.getBoitier());

                            //ValeurParametre valeurParametre = valeurParametres.stream().filter(x -> x.getParametre().getIndicateur() == mesure.getIndicateur()).findAny().orElse(null);

                            // Alerte seuils




                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            }
        }

        // Retourner les paramètres du boitier
        return new ApiResponse(formatParameters(boitierService.findBoitierParametre(boitier)),"OK",
                HttpStatus.OK.value()) ;
    }

    @RequestMapping(value = "/mesureByDate", method = RequestMethod.POST)
    public ApiResponse mesureByDate(@RequestBody RequestMesure requestMesure) {

        return new ApiResponse(
                mesureService.listMesureByDate(
                        requestMesure.getIdBoitier(),
                        requestMesure.getIdIndicateur(),
                        requestMesure.getDateDebut(),
                        requestMesure.getDateFin()),
                "OK",
                HttpStatus.OK.value()
        );

    }

    @RequestMapping(value = "/lastMesures", method = RequestMethod.POST)
    public ApiResponse lastMesures(@RequestBody RequestMesure requestMesure) {

        return new ApiResponse(
                mesureService.lastMesures(
                        requestMesure.getIdBoitier()),
                "OK",
                HttpStatus.OK.value()
        );

    }

    @RequestMapping(value = "/allMesuresByDate", method = RequestMethod.POST)
    public ApiResponse allMesuresByDate(@RequestBody RequestMesure requestMesure) {

        return new ApiResponse(
                mesureService.allMesuresByDate(
                        requestMesure.getIdBoitier(),
                        requestMesure.getDateDebut(),
                        requestMesure.getDateFin()),
                "OK",
                HttpStatus.OK.value()
        );

    }

    @RequestMapping(value = "/brutes", method = RequestMethod.POST)
    public ApiResponse mesuresBrutes(@RequestBody RequestMesure requestMesure) {

        return new ApiResponse(
                mesureBrutService.brutesByDate(
                        requestMesure.getDateDebut(),
                        requestMesure.getDateFin()),
                "OK",
                HttpStatus.OK.value()
        );

    }

    public Object formatParameters(List<ValeurParametre> valeurParametres) {
        String retour="";
        if (!valeurParametres.isEmpty())
            for (ValeurParametre valeurParametre : valeurParametres) {
                retour += valeurParametre.getValeur()  ; //  + "," + valeurParametre.getParametre().getId() + ";"
            }
        return retour;
    }

}
