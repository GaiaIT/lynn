package net.gaiait.divination.symbol.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import net.gaiait.divination.symbol.SymbolService;
import net.gaiait.divination.symbol.generative.GenerativeSymbolConstants;

@Controller
final class ViewController {

    @Autowired private GenerativeSymbolConstants generativeSymbolConstants;
    @Autowired private SymbolService symbolService;
    
    @RequestMapping("/")
    public String home(Model model) {
        return "home";
    }

    @RequestMapping("/create")
    public String create(Model model) {
        model.addAttribute("sc", generativeSymbolConstants);
        return "create";
    }

    @RequestMapping("/sandbox")
    public String sandbox(Model model) {
        model.addAttribute("sc", generativeSymbolConstants);   
        return "sandbox";
    }

    @RequestMapping("/fortune/{id}")
    public String fortune(@PathVariable long id, Model model) {
        if (symbolService.symbolExists(id)) {
            return "fortune";
        } else {
            return "error";
        }
    }

}
