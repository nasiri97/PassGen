package ir.ornix.passgen.autofill

import android.os.Parcel
import android.os.Parcelable
import android.view.autofill.AutofillId

class ParsedStructure(
    val usernameId: AutofillId? = null,
    val emailId: AutofillId? = null,
    val passwordId: AutofillId? = null,
    val phoneId: AutofillId? = null,
    val genericIds: List<AutofillId> = emptyList(),
    val packageName: String? = null,
    val webDomain: String? = null,
    val extractedKeywords: Set<String> = emptySet(),
    val targetFieldWidthDp: Int = 280
) : Parcelable {

    val hasAutofillableFields: Boolean
        get() = usernameId != null || emailId != null || passwordId != null || phoneId != null || genericIds.isNotEmpty()

    val primaryAutofillId: AutofillId?
        get() = passwordId ?: usernameId ?: emailId ?: phoneId ?: genericIds.firstOrNull()

    @Suppress("DEPRECATION")
    constructor(parcel: Parcel) : this(
        usernameId = parcel.readParcelable(AutofillId::class.java.classLoader),
        emailId = parcel.readParcelable(AutofillId::class.java.classLoader),
        passwordId = parcel.readParcelable(AutofillId::class.java.classLoader),
        phoneId = parcel.readParcelable(AutofillId::class.java.classLoader),
        genericIds = mutableListOf<AutofillId>().also { list ->
            parcel.readTypedList(list, AutofillId.CREATOR)
        },
        packageName = parcel.readString(),
        webDomain = parcel.readString(),
        extractedKeywords = parcel.createStringArrayList()?.toSet() ?: emptySet(),
        targetFieldWidthDp = parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(usernameId, flags)
        parcel.writeParcelable(emailId, flags)
        parcel.writeParcelable(passwordId, flags)
        parcel.writeParcelable(phoneId, flags)
        parcel.writeTypedList(genericIds)
        parcel.writeString(packageName)
        parcel.writeString(webDomain)
        parcel.writeStringList(extractedKeywords.toList())
        parcel.writeInt(targetFieldWidthDp)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ParsedStructure> {
        override fun createFromParcel(parcel: Parcel): ParsedStructure {
            return ParsedStructure(parcel)
        }

        override fun newArray(size: Int): Array<ParsedStructure?> {
            return arrayOfNulls(size)
        }
    }
}
