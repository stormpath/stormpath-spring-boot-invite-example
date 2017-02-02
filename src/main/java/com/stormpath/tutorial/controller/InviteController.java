/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.tutorial.controller;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.directory.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Controller
public class InviteController {

    @Value("#{ @environment['stormpath.invite.directory.href'] }")
    private String inviteDirectoryHref;

    private Client client;
    private Directory inviteDirectory;

    @Autowired
    public InviteController(Client client) {
        this.client = client;
    }

    @PostConstruct
    void setup() {
        inviteDirectory = client.getResource(inviteDirectoryHref, Directory.class);
    }

    @RequestMapping("/invite")
    public String invite(
        @RequestParam String email, @RequestParam String givenName, @RequestParam String surName, Model model
    ) {
        Account account = client.instantiate(Account.class);

        account
            .setEmail(email)
            .setGivenName(givenName)
            .setSurname(surName)
            .setPassword("A0" + UUID.randomUUID().toString());

        inviteDirectory.createAccount(account);

        model.addAttribute("email", email);

        return "invite_confirm";
    }

    @RequestMapping("/emailVerificationTokens")
    public String verify(@RequestParam String sptoken) {
        client.verifyAccountEmail(sptoken);

        // if we are here, the account is verified, proceed with registration

        return "redirect:/register";
    }
}
