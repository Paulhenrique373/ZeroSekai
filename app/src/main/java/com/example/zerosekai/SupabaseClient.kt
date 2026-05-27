package com.example.zerosekai

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

val supabase = createSupabaseClient(

    supabaseUrl = "https://hnxhkrzjbueoootxinvt.supabase.co",

    supabaseKey = "sb_publishable_KJTonv7D_HoPUZtB5GFfDQ_rIZenRBY"

) {

    install(Postgrest)
    install(Storage)
}
