package org.mifos.application.office.persistence;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.OfficeCacheView;

public class OfficePersistence extends Persistence {

	public OfficePersistence() {
		super();
	}

	public List<OfficeView> getActiveOffices(Short officeId)
			throws PersistenceException {
		String searchId = HierarchyManager.getInstance().getSearchId(officeId);
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("levelId", OfficeConstants.BRANCHOFFICE);
		queryParameters.put("OFFICESEARCHID", searchId);
		queryParameters.put("OFFICE_LIKE_SEARCHID", searchId + ".%");
		queryParameters.put("statusId", OfficeConstants.ACTIVE);
		List<OfficeView> queryResult = executeNamedQuery(
				NamedQueryConstants.MASTERDATA_ACTIVE_BRANCHES, queryParameters);
		return queryResult;

	}

	public List<OfficeCacheView> getAllOffices() throws PersistenceException {
		return executeNamedQuery(NamedQueryConstants.GET_ALL_OFFICES, null);
	}

	public String getSearchId(Short officeId) throws PersistenceException {
		String searchId = "";
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		List<String> queryResult = executeNamedQuery(
				NamedQueryConstants.OFFICE_GET_SEARCHID, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			searchId = queryResult.get(0);
		}
		return searchId;
	}

	/**
	 * @return The office, or null if not found (TODO: wouldn't we rather
	 * have an exception if not found?  The usual idiom seems to be to just
	 * dereference the returned office without any checking for null)
	 */
	public OfficeBO getOffice(Short officeId) throws PersistenceException {
		return (OfficeBO) getPersistentObject(OfficeBO.class, officeId);
	}

	public OfficeBO getHeadOffice() throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", OfficeConstants.HEADOFFICE);
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.OFFICE_GET_HEADOFFICE, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult.get(0);
		}
		return null;
	}

	public Short getMaxOfficeId() throws PersistenceException {
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GETMAXOFFICEID, null);
		if (queryResult != null && queryResult.size() != 0) {
			return (Short) queryResult.get(0);
		}
		return null;
	}

	public Integer getChildCount(Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		List queryResult = executeNamedQuery(NamedQueryConstants.GETCHILDCOUNT,
				queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).intValue();
		}
		return null;

	}

	public boolean isOfficeNameExist(String officeName)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_NAME", officeName);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.CHECKOFFICENAMEUNIQUENESS, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).longValue() > 0;
		}
		return false;
	}

	public boolean isOfficeShortNameExist(String shortName)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SHORT_NAME", shortName);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.CHECKOFFICESHORTNAMEUNIQUENESS,
				queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).longValue() > 0;
		}
		return false;
	}

	public boolean hasActiveChildern(Short officeId)
			throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GETCOUNTOFACTIVECHILDERN, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).longValue() > 0;
		}
		return false;
	}

	public boolean hasActivePeronnel(Short officeId)
			throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		queryParameters.put("STATUS_ID", PersonnelConstants.ACTIVE);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GETOFFICEACTIVEPERSONNEL, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).longValue() > 0;
		}
		return false;
	}

	public boolean isBranchInactive(short officeId) throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		queryParameters.put("STATUS_ID", OfficeConstants.INACTIVE);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GETOFFICEINACTIVE, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return ((Number) queryResult.get(0)).longValue() > 0;
		}
		return false;
	}

	public List<OfficeView> getActiveParents(OfficeLevel level, Short localeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", level.getValue());
		queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
		queryParameters.put("LOCALE_ID", localeId);
		List<OfficeView> queryResult = executeNamedQuery(
				NamedQueryConstants.GETACTIVEPARENTS, queryParameters);
			return queryResult;

	}

	public List<OfficeView> getActiveLevels(Short localeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LOCALE_ID", localeId);
		List<OfficeView> queryResult = executeNamedQuery(
				NamedQueryConstants.GETACTIVELEVELS, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;

	}

	public List<OfficeView> getStatusList(Short localeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LOCALE_ID", localeId);
		List<OfficeView> queryResult = executeNamedQuery(
				NamedQueryConstants.GETOFFICESTATUS, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getChildern(Short officeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GETCHILDERN, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getOfficesTillBranchOffice()
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters
				.put("branchOffice", OfficeLevel.BRANCHOFFICE.getValue());
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_OFFICES_TILL_BRANCHOFFICE,
				queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getBranchOffices() throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters
				.put("branchOffice", OfficeLevel.BRANCHOFFICE.getValue());
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_BRANCH_OFFICES, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getOfficesTillBranchOffice(String searchId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", OfficeLevel.BRANCHOFFICE.getValue());
		queryParameters.put("SEARCH_ID", searchId + "%");
		queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_OFFICES_TILL_BRANCH, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getBranchParents(String searchId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", OfficeLevel.BRANCHOFFICE.getValue());
		queryParameters.put("SEARCH_ID", searchId + "%");
		queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
		List<OfficeBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_BRANCH_PARENTS, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeView> getChildOffices(String searchId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("STATUS_ID", OfficeStatus.ACTIVE.getValue());
		queryParameters.put("OFFICE_LIKE_SEARCHID", searchId + "%");
		List<OfficeView> queryResult = executeNamedQuery(
				NamedQueryConstants.GETOFFICE_CHILDREN, queryParameters);
		if (queryResult != null && queryResult.size() != 0) {
			return queryResult;
		}
		return null;
	}

	public List<OfficeBO> getActiveBranchesUnderUser(String officeSearchId)throws PersistenceException{
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerSearchConstants.OFFICELEVELID, OfficeLevel.BRANCHOFFICE.getValue());
		queryParameters.put(CustomerSearchConstants.OFFICESEARCHID, officeSearchId + "%");
		queryParameters.put(OfficeConstants.OFFICE_ACTIVE, OfficeStatus.ACTIVE.getValue());
		
		return executeNamedQuery(
			NamedQueryConstants.GET_ACTIVE_BRANCHES, queryParameters);
	}
}
