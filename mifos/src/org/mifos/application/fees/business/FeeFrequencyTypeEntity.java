package org.mifos.application.fees.business;

import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.master.business.MasterDataEntity;

public class FeeFrequencyTypeEntity extends MasterDataEntity {

	public FeeFrequencyTypeEntity(FeeFrequencyType feeFrequency) {
		super(feeFrequency.getValue());
	}

	protected FeeFrequencyTypeEntity() {
	}

	public boolean isPeriodic() {
		return getId().equals(FeeFrequencyType.PERIODIC.getValue());
	}

	public boolean isOneTime() {
		return getId().equals(FeeFrequencyType.ONETIME.getValue());
	}
}
