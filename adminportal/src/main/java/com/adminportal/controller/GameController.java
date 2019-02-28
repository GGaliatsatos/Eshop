package com.adminportal.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.adminportal.domain.Game;
import com.adminportal.domain.Language;


import com.adminportal.service.GameService;
import com.adminportal.service.LanguageService;





@Controller
@RequestMapping("/game")
public class GameController {

	@Autowired
	private GameService gameService;

	@Autowired
	private LanguageService languageService;
	

	
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addGame(Model model) {
		Game game = new Game();

		model.addAttribute("game", game);

		
		return "addGame";
	}

	// CREATE
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addGamePost(@ModelAttribute("game") Game game, 
			BindingResult bindingResult, HttpServletRequest request) {
		String[] validLanguages = getValues(com.adminportal.domain.enumeration.Language.class);
		game.getLanguageList().removeIf(lang -> lang.getName() == null);
		for(Language language: game.getLanguageList()) {
			if(language.getName() != null ) {
				// check if language has a valid value
				if( Arrays.stream(validLanguages).anyMatch(language.getName()::equals)) {					
					language.setGame(game);
				}
			}
		}

		try {			
			gameService.save(game);
		} catch (Exception e) {
			e.printStackTrace();
		}

		MultipartFile gameImage = game.getGameImage();

		try {
			byte[] bytes = gameImage.getBytes();
			String name = game.getId() + ".jpg";
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File("src/main/resources/static/image/game/" + name)));
			stream.write(bytes);
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:gameList";
	}

	// READ
	@RequestMapping("/gameInfo")
	public String gameInfo(@RequestParam("id") Long id, Model model) {
		Game game = gameService.findOne(id);
		
		//Date format conversion
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat shownDF = new SimpleDateFormat("dd-MM-yyyy");
		try {
			Date date = df.parse(game.getReleaseDate());
			String releaseDate = shownDF.format(date);
			game.setReleaseDate(releaseDate); // show with 'dd-MM-yyyy' format.			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("game", game);
		
		return "gameInfo";
	}
	
	@RequestMapping("/gameList")
	public String gameList(Model model) {
		List<Game> gameList = gameService.findAll();
		model.addAttribute("gameList", gameList);		
		return "gameList";
		
	}
	
	//UPDATE
	@RequestMapping("/updateGame")
	public String updateGame(@RequestParam("id") Long id, Model model) {
		Game game = gameService.findOne(id);
		int i = 0;
		for(com.adminportal.domain.enumeration.Language language : com.adminportal.domain.enumeration.Language.values()) {
			if(game.getLanguageList().size() > i) {	// to avoid ArrayList range-check exception.			
				if(!game.getLanguageList().get(i).getName().equals(language.toString())) {
					game.getLanguageList().add(i, new Language());
				}
			}
			i++;
		}
		for(Language language : game.getLanguageList()) {
			
			System.out.println(language.getName());
		}
		model.addAttribute("game", game);
		return "updateGame";
	}
	
	@RequestMapping(value="/updateGame", method=RequestMethod.POST)
	public String updateGamePost(@ModelAttribute("game") Game game ,  
			BindingResult result, HttpServletRequest request) {	
		// Delete all language records prior to the update
		languageService.deleteByGame(game);
		String[] validLanguages = getValues(com.adminportal.domain.enumeration.Language.class);
		game.getLanguageList().removeIf(lang -> lang.getName() == null);
		for(Language language: game.getLanguageList()) {
			if(language.getName() != null ) {
				// check if language has a valid value
				if( Arrays.stream(validLanguages).anyMatch(language.getName()::equals)) {					
					language.setGame(game);
				}
			}
		}
		System.out.println(game.getWinOS());
		gameService.save(game);
		MultipartFile gameImage = game.getGameImage();
		if(!gameImage.isEmpty()) {
			try {
				byte[] bytes = gameImage.getBytes();
				String name = game.getId() + ".jpg";
				deleteImage(name);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File("src/main/resources/static/image/game/" + name)));
				stream.write(bytes);
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "redirect:/game/gameInfo?id="+game.getId();
	}
	
	
	// separated method in case of not uploaded image
	public void deleteImage(String name) {
		try {
		Files.delete(Paths.get("src/main/resources/static/image/game/"+name));
		return;
		}catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	
	//DELETE
	
	@RequestMapping(value="/remove", method=RequestMethod.POST)
	public String remove(
			@ModelAttribute("id") String id, Model model
			) {
		gameService.delete(Long.parseLong(id));
		List<Game> gameList = gameService.findAll();
		model.addAttribute("gameList", gameList);
		
		return "redirect:/game/gameList";
	}

	public static String[] getValues(Class<? extends Enum<?>> e) {
	    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	

}
