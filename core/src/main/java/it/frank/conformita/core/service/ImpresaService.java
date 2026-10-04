package it.frank.conformita.core.service;

import it.frank.conformita.core.dto.ImpresaDto;
import it.frank.conformita.core.entity.Impresa;
import it.frank.conformita.core.mapper.EntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class ImpresaService {

    private static final long IMPRESA_ID = 1L;

    private final EntityManagerFactory entityManagerFactory;

    public ImpresaService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Optional<ImpresaDto> getImpresa() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Impresa impresa = em.find(Impresa.class, IMPRESA_ID);
            if (impresa == null) {
                return Optional.empty();
            }
            return Optional.of(EntityMapper.toDto(impresa));
        } finally {
            em.close();
        }
    }

    public ImpresaDto saveImpresa(ImpresaDto dto) {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Impresa impresa = em.find(Impresa.class, IMPRESA_ID);
            if (impresa == null) {
                impresa = new Impresa();
                impresa.setId(IMPRESA_ID);
                em.persist(impresa);
            }
            EntityMapper.apply(impresa, dto);
            em.merge(impresa);
            tx.commit();
            return EntityMapper.toDto(impresa);
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Impresa loadImpresaEntity(EntityManager em) {
        Impresa impresa = em.find(Impresa.class, IMPRESA_ID);
        return impresa;
    }
}
