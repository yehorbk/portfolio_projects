package space.generics.superburger.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import space.generics.superburger.dto.promotion.AddPromotionDto;
import space.generics.superburger.dto.promotion.PromotionDto;
import space.generics.superburger.dto.promotion.UpdatePromotionDto;
import space.generics.superburger.entity.Promotion;
import space.generics.superburger.exception.exceptions.EntityByValueNotFoundException;
import space.generics.superburger.repository.PromotionRepository;
import space.generics.superburger.service.FileService;
import space.generics.superburger.service.PromotionService;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final FileService fileService;
    
    private final PromotionRepository promotionRepository;
    
    private final ModelMapper modelMapper;

    public PromotionServiceImpl(
            FileService fileService,
            PromotionRepository promotionRepository,
            ModelMapper modelMapper) {
        this.fileService = fileService;
        this.promotionRepository = promotionRepository;
        this.modelMapper = modelMapper;
    }
    
    Promotion findById(long id) {
        Optional<Promotion> optional = this.promotionRepository.findById(id);
        if (optional.isPresent()) {
            Promotion result = optional.get();
            return result;
        }
        throw new EntityByValueNotFoundException(
                "Promotion", "id", String.valueOf(id));
    }
    
    @Override
    public PromotionDto readById(long id) {
        Promotion result = this.findById(id);
        return this.modelMapper.map(result, PromotionDto.class);
    }

    @Override
    public PromotionDto create(AddPromotionDto dto) {
        Promotion promotion = this.modelMapper.map(dto, Promotion.class);
        Promotion result = this.promotionRepository.save(promotion);
        return this.modelMapper.map(result, PromotionDto.class);
    }

    @Override
    public PromotionDto update(long id, UpdatePromotionDto dto) {
        Promotion promotion = this.findById(id);
        this.modelMapper.map(dto, promotion);
        Promotion result = this.promotionRepository.save(promotion);
        return this.modelMapper.map(result, PromotionDto.class);
    }
    
    @Override
    public PromotionDto updateImageById(long id, MultipartFile image) {
        Promotion promotion = this.findById(id);
        String path = this.fileService.upload(image);
        promotion.setImageSrc(path);
        Promotion result = this.promotionRepository.save(promotion);
        return this.modelMapper.map(result, PromotionDto.class);
    }

    @Override
    public void deleteById(long id) {
        Promotion promotion = this.findById(id);
        this.promotionRepository.delete(promotion);
    }

    @Override
    public List<PromotionDto> readAll() {
        Function<Promotion, PromotionDto> mapper = promotion ->
                this.modelMapper.map(promotion, PromotionDto.class);
        List<Promotion> result = this.promotionRepository.findAll();
        return result.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
}
