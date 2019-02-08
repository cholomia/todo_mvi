package com.mundomo.fdmmia.todo.domain.enums

enum class DateTimeFormat {
    DATE_LONG {
        override val value: String
            get() = "MMMM dd, yyyy"
    },
    DATE_TIME_SHORT {
        override val value: String
            get() = "yyyy-MM-dd HH:mm:ss"
    },
    COUNTDOWN_FORMAT {
        override val value: String
            get() = "%02d days %02d hours %02d minutes %02d seconds"
    },
    DATE_ONLY {
        override val value: String
            get() = "yyyy-MM-dd"
    };

    abstract val value: String
}
