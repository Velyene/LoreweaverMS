package com.example.encountertimer.domain.repository

interface CampaignRepository :
	CampaignsRepository,
	EncountersRepository,
	SessionsRepository,
	NotesRepository,
	CharactersRepository,
	LogsRepository
