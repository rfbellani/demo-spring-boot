package br.com.demo.controller;

import br.com.demo.controller.dto.DetalhesDoTopicoDto;
import br.com.demo.controller.dto.TopicoDto;
import br.com.demo.controller.form.AtualizacaoTopicoForm;
import br.com.demo.controller.form.TopicoForm;
import br.com.demo.model.Curso;
import br.com.demo.model.Topico;
import br.com.demo.repository.CursoRepository;
import br.com.demo.repository.TopicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicosController {

    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private CursoRepository cursoRepository;

    //@RequestMapping(value="/topicos" , method = RequestMethod.GET)
    @GetMapping
    public List<TopicoDto> lista(String nomeCurso){

        if (nomeCurso == null){
            List<Topico> topicoList = topicoRepository.findAll();
            return TopicoDto.converter(topicoList);
        } else {
            List<Topico> topicoList = topicoRepository.findByCursoNome(nomeCurso);
            return TopicoDto.converter(topicoList);
        }

    }

    //@RequestMapping(value="/topicos" , method = RequestMethod.POST)
    @PostMapping
    @Transactional
    public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm topicoForm, UriComponentsBuilder uriBuilder){
        Topico topico = topicoForm.converter(cursoRepository);
        topicoRepository.save(topico);

        URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
        return ResponseEntity.created(uri).body(new TopicoDto(topico));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhesDoTopicoDto> detalhar(@PathVariable Long id){
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            return ResponseEntity.ok(new DetalhesDoTopicoDto(topicoOptional.get()));

        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm topicoForm){
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        
        if (topicoOptional.isPresent()) {
            Topico topico = topicoForm.atualizar(id, this.topicoRepository);
            return ResponseEntity.ok(new TopicoDto(topico));
        }

        return ResponseEntity.notFound().build();

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity remover(@PathVariable Long id){
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        if (topicoOptional.isPresent()) {
            topicoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }


}
