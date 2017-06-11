package money.fluid.ilp.ledger.model.ids;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.immutables.value.Value.Style.ImplementationVisibility;

// declare style as meta annotation as shown
// or on package/top-level class
// This is just an example, adapt to your taste however you like
@Style(
        // Detect names starting with underscore
        typeAbstract = "_*",
        // Generate without any suffix, just raw detected name
        typeImmutable = "*",
        // Make generated it public, leave underscored as package private
        visibility = ImplementationVisibility.PUBLIC,
        // Seems unnecessary to have builder or superfluous copy method
        defaults = @Immutable(builder = false, copy = false))
public @interface Wrapped {}

// base wrapper type
abstract class Wrapper<T> {
    @Value.Parameter
    public abstract T value();
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + value() + ")";
    }
}

// Declare wrapper types/domain values

@Value.Immutable @Wrapped
abstract class _EscrowAccountId extends Wrapper<String> {}

@Value.Immutable @Wrapped
abstract class _LedgerId extends Wrapper<String> {}

@Value.Immutable @Wrapped
abstract class _LedgerAccountId extends Wrapper<String> {}

@Value.Immutable @Wrapped
abstract class _LedgerTransferId extends Wrapper<String> {}