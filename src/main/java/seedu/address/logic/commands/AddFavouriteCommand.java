package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Favourite;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.ProfPic;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Marks an indexed person as a favourite in the address book.
 */
public class AddFavouriteCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "fadd";
    public static final String COMMAND_ALT = "fa";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the person identified by the index number used in the last person listing as a favourite.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_FAVE_PERSON_SUCCESS = "%1$s has been marked as a favourite contact.";
    public static final String MESSAGE_ALREADY_FAVOURITE = "This person is already marked as a favourite.";

    private final Index targetIndex;

    public AddFavouriteCommand(Index index) {
        requireNonNull(index);
        this.targetIndex = index;
    }

    @Override
    public CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToEdit = lastShownList.get(targetIndex.getZeroBased());
        Person editedPerson;

        try {
            editedPerson = createFavePerson(personToEdit);
            model.updatePerson(personToEdit, editedPerson);
        } catch (DuplicatePersonException dpe) {
            throw new CommandException(MESSAGE_ALREADY_FAVOURITE);
        } catch (PersonNotFoundException pnfe) {
            throw new AssertionError("The target person cannot be missing");
        }
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_FAVE_PERSON_SUCCESS, editedPerson));
    }

    /**
     * Creates and returns a {@code Person} with the the Favourite attribute set to true.
     */
    private static Person createFavePerson(ReadOnlyPerson personToEdit) throws DuplicatePersonException {
        if (personToEdit.getFavourite().getStatus()) {
            throw new DuplicatePersonException();
        }
        Name updatedName = personToEdit.getName();
        Phone updatedPhone = personToEdit.getPhone();
        Email updatedEmail = personToEdit.getEmail();
        Address updatedAddress = personToEdit.getAddress();
        ProfPic updatedProfPic = personToEdit.getProfPic();
        Favourite updatedFavourite = new Favourite(true);
        Set<Tag> updatedTags = personToEdit.getTags();

        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress, updatedFavourite,
                updatedProfPic, updatedTags);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddFavouriteCommand // instanceof handles nulls
                && this.targetIndex.equals(((AddFavouriteCommand) other).targetIndex)); // state check
    }
}
