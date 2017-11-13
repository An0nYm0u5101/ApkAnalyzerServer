package sk.styk.martin.apkanalyzer.dao.impl;

import org.hibernate.validator.constraints.NotEmpty;
import sk.styk.martin.apkanalyzer.dao.AppDataDao;
import sk.styk.martin.apkanalyzer.entity.AppData;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRED)
public class AppDataDaoImpl extends GenericDaoImpl<AppData, Long> implements AppDataDao, Serializable {

    public AppDataDaoImpl() {
        super(AppData.class);
    }

    @Override
    public List<AppData> getForDevice(@NotEmpty String deviceId) {
        TypedQuery<AppData> q = em.createQuery("SELECT a FROM AppData a WHERE a.androidId = :deviceId",
                AppData.class).setParameter("deviceId", deviceId);
        return q.getResultList();
    }
}