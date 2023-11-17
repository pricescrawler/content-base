package io.github.pricescrawler.content.repository.product;

import io.github.pricescrawler.content.common.dao.product.ProductDao;
import io.github.pricescrawler.content.common.dto.product.ProductDto;
import io.github.pricescrawler.content.common.util.IdUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimpleProductDataService implements ProductDataService {
    private final ProductDataRepository productDataRepository;

    @Override
    public List<ProductDao> findProducts(String locale, String catalog, String reference) {
        return productDataRepository.findAllById(IdUtils.parse(locale, catalog, reference));
    }

    @Override
    public List<ProductDao> findProductsByEanUpc(String eanUpc) {
        return productDataRepository.findAllByEanUpcList(eanUpc);
    }

    @Override
    public void save(List<ProductDto> productDtoList) {
        var products = productDtoList.stream().map(value ->
                        ProductDao.builder()
                                .id(value.getId())
                                .reference(value.getReference())
                                .name(value.getName())
                                .regularPrice(value.getRegularPrice())
                                .campaignPrice(value.getRegularPrice())
                                .pricePerQuantity(value.getPricePerQuantity())
                                .quantity(value.getQuantity())
                                .brand(value.getBrand())
                                .description(value.getDescription())
                                .productUrl(value.getProductUrl())
                                .imageUrl(value.getImageUrl())
                                .eanUpcList(value.getEanUpcList())
                                .date(value.getDate())
                                .data(value.getData())
                                .build()
                )
                .toList();

        productDataRepository.saveAll(products);
    }
}
