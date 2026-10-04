package it.frank.conformita.core.service;

import it.frank.conformita.core.dto.PraticaDto;
import it.frank.conformita.core.dto.PraticaSummary;
import it.frank.conformita.core.entity.Pratica;
import it.frank.conformita.core.entity.StatoPratica;
import it.frank.conformita.core.mapper.EntityMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.util.List;

public class PraticaService {

    private final EntityManagerFactory entityManagerFactory;

    public PraticaService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<PraticaSummary> listPratiche() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            return em.createQuery("select p from Pratica p order by p.updatedAt desc", Pratica.class)
                    .getResultStream()
                    .map(EntityMapper::toSummary)
                    .toList();
        } finally {
            em.close();
        }
    }

    public PraticaDto createPratica() {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Pratica pratica = new Pratica();
            pratica.setStato(StatoPratica.BOZZA);
            Instant now = Instant.now();
            pratica.setCreatedAt(now);
            pratica.setUpdatedAt(now);
            em.persist(pratica);
            tx.commit();
            return EntityMapper.toDto(pratica);
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public PraticaDto loadPratica(long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            Pratica pratica = em.find(Pratica.class, id);
            if (pratica == null) {
                throw new IllegalArgumentException("Pratica non trovata: " + id);
            }
            pratica.getMateriali().size();
            pratica.getVerifiche().size();
            return EntityMapper.toDto(pratica);
        } finally {
            em.close();
        }
    }

    public PraticaDto savePratica(PraticaDto dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Pratica id mancante");
        }
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Pratica pratica = em.find(Pratica.class, dto.id());
            if (pratica == null) {
                throw new IllegalArgumentException("Pratica non trovata: " + dto.id());
            }
            EntityMapper.apply(pratica, dto);
            pratica.setUpdatedAt(Instant.now());
            em.merge(pratica);
            tx.commit();
            return loadPratica(dto.id());
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void deletePratica(long id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        var tx = em.getTransaction();
        tx.begin();
        try {
            Pratica pratica = em.find(Pratica.class, id);
            if (pratica == null) {
                throw new IllegalArgumentException("Pratica non trovata: " + id);
            }
            em.remove(pratica);
            tx.commit();
        } catch (RuntimeException e) {
            tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
