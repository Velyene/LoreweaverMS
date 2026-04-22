##############################################################
# Test-DndApi.ps1
# Verifies every dnd5eapi.co endpoint used by Loreweaver.
# Run: powershell -ExecutionPolicy Bypass -File .\scripts\Test-DndApi.ps1
##############################################################

$BASE    = "https://www.dnd5eapi.co/api/2014"
$headers = @{ "Accept" = "application/json" }
$passed  = 0
$failed  = 0

function Pass($label, $info) {
    Write-Host "  [PASS] $label" -ForegroundColor Green
    if ($info) { Write-Host "         $info" -ForegroundColor DarkGray }
    $script:passed++
}

function Fail($label, $reason) {
    Write-Host "  [FAIL] $label -- $reason" -ForegroundColor Red
    $script:failed++
}

##############################################################
# 1. ROOT
##############################################################
Write-Host "`n=== Root ===" -ForegroundColor Cyan
try {
    $r = Invoke-RestMethod "$BASE" -Method GET -Headers $headers
    if ($r.monsters) { Pass "Root endpoint reachable" "monsters path = $($r.monsters)" }
    else             { Fail "Root endpoint reachable" "monsters key missing" }
} catch { Fail "Root endpoint reachable" $_.Exception.Message }

##############################################################
# 2. LIST ENDPOINTS  (returns {count, results:[{index,name,url}]})
##############################################################
Write-Host "`n=== List Endpoints ===" -ForegroundColor Cyan

$lists = @(
    @{ L = "Monsters list";    U = "$BASE/monsters" },
    @{ L = "Spells list";      U = "$BASE/spells" },
    @{ L = "Classes list";     U = "$BASE/classes" },
    @{ L = "Backgrounds list"; U = "$BASE/backgrounds" },
    @{ L = "Feats list";       U = "$BASE/feats" },
    @{ L = "Conditions list";  U = "$BASE/conditions" },
    @{ L = "Races list";       U = "$BASE/races" },
    @{ L = "Magic Items list"; U = "$BASE/magic-items" }
)

foreach ($ep in $lists) {
    try {
        $r     = Invoke-RestMethod $ep.U -Method GET -Headers $headers
        $first = $r.results | Select-Object -First 1
        if ($r.count -gt 0 -and $first.index) {
            Pass $ep.L "count=$($r.count)  first=[$($first.index)] $($first.name)"
        } else {
            Fail $ep.L "count=0 or missing index"
        }
    } catch { Fail $ep.L $_.Exception.Message }
}

##############################################################
# 3. EQUIPMENT-CATEGORY ENDPOINTS  (returns {equipment:[...]})
##############################################################
Write-Host "`n=== Equipment-Category List Endpoints ===" -ForegroundColor Cyan

try {
    $r = Invoke-RestMethod "$BASE/equipment-categories/weapon" -Method GET -Headers $headers
    $first = $r.equipment | Select-Object -First 1
    if ($r.equipment.Count -gt 0) { Pass "Weapons list" "count=$($r.equipment.Count)  first=[$($first.index)] $($first.name)" }
    else { Fail "Weapons list" "empty equipment array" }
} catch { Fail "Weapons list" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/equipment-categories/armor" -Method GET -Headers $headers
    $first = $r.equipment | Select-Object -First 1
    if ($r.equipment.Count -gt 0) { Pass "Armor list" "count=$($r.equipment.Count)  first=[$($first.index)] $($first.name)" }
    else { Fail "Armor list" "empty equipment array" }
} catch { Fail "Armor list" $_.Exception.Message }

##############################################################
# 4. DETAIL ENDPOINTS
##############################################################
Write-Host "`n=== Detail Endpoints ===" -ForegroundColor Cyan

try {
    $r = Invoke-RestMethod "$BASE/monsters/aboleth" -Method GET -Headers $headers
    if ($r.hit_points) { Pass "Monster detail -- aboleth" "CR=$($r.challenge_rating)  type=$($r.type)  HP=$($r.hit_points)  AC=$($r.armor_class[0].value)" }
    else { Fail "Monster detail -- aboleth" "hit_points missing" }
} catch { Fail "Monster detail -- aboleth" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/spells/fireball" -Method GET -Headers $headers
    if ($r.desc) {
        $snip = ($r.desc -join " ").Substring(0, [Math]::Min(70, ($r.desc -join " ").Length))
        Pass "Spell detail -- fireball" "level=$($r.level)  school=$($r.school.name)"
        Write-Host "         desc: $snip..." -ForegroundColor DarkGray
    } else { Fail "Spell detail -- fireball" "desc missing" }
} catch { Fail "Spell detail -- fireball" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/classes/barbarian" -Method GET -Headers $headers
    if ($r.hit_die) { Pass "Class detail -- barbarian" "hit_die=$($r.hit_die)" }
    else { Fail "Class detail -- barbarian" "hit_die missing" }
} catch { Fail "Class detail -- barbarian" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/backgrounds/acolyte" -Method GET -Headers $headers
    if ($r.feature) {
        $profs = ($r.starting_proficiencies | ForEach-Object { $_.name }) -join ", "
        Pass "Background detail -- acolyte" "feature=$($r.feature.name)  profs=$profs"
    } else { Fail "Background detail -- acolyte" "feature missing" }
} catch { Fail "Background detail -- acolyte" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/feats/grappler" -Method GET -Headers $headers
    if ($r.desc) {
        $prereq = "$($r.prerequisites[0].ability_score.name) >= $($r.prerequisites[0].minimum_score)"
        $snip   = ($r.desc -join " ").Substring(0, [Math]::Min(70, ($r.desc -join " ").Length))
        Pass "Feat detail -- grappler" "prereq=$prereq"
        Write-Host "         desc: $snip..." -ForegroundColor DarkGray
    } else { Fail "Feat detail -- grappler" "desc missing" }
} catch { Fail "Feat detail -- grappler" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/conditions/blinded" -Method GET -Headers $headers
    if ($r.desc) {
        $snip = ($r.desc -join " ").Substring(0, [Math]::Min(70, ($r.desc -join " ").Length))
        Pass "Condition detail -- blinded" "index=$($r.index)"
        Write-Host "         desc: $snip..." -ForegroundColor DarkGray
    } else { Fail "Condition detail -- blinded" "desc missing" }
} catch { Fail "Condition detail -- blinded" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/races/human" -Method GET -Headers $headers
    if ($r.size) { Pass "Race detail -- human" "size=$($r.size)  speed=$($r.speed)ft" }
    else { Fail "Race detail -- human" "size missing" }
} catch { Fail "Race detail -- human" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/magic-items/bag-of-holding" -Method GET -Headers $headers
    if ($r.rarity) {
        $snip = ($r.desc -join " ").Substring(0, [Math]::Min(70, ($r.desc -join " ").Length))
        Pass "Magic Item detail -- bag-of-holding" "rarity=$($r.rarity.name)"
        Write-Host "         desc: $snip..." -ForegroundColor DarkGray
    } else { Fail "Magic Item detail -- bag-of-holding" "rarity missing" }
} catch { Fail "Magic Item detail -- bag-of-holding" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/equipment/longsword" -Method GET -Headers $headers
    if ($r.damage) { Pass "Weapon detail -- longsword" "category=$($r.weapon_category)  damage=$($r.damage.damage_dice)" }
    else { Fail "Weapon detail -- longsword" "damage missing" }
} catch { Fail "Weapon detail -- longsword" $_.Exception.Message }

try {
    $r = Invoke-RestMethod "$BASE/equipment/chain-mail" -Method GET -Headers $headers
    if ($r.armor_class) { Pass "Armor detail -- chain-mail" "category=$($r.armor_category)  AC_base=$($r.armor_class.base)  str_min=$($r.str_minimum)  stealth=$($r.stealth_disadvantage)" }
    else { Fail "Armor detail -- chain-mail" "armor_class missing" }
} catch { Fail "Armor detail -- chain-mail" $_.Exception.Message }

##############################################################
# 5. CONDITION SLUG FORMAT  (mirrors CharacterDetailScreen lookup)
##############################################################
Write-Host "`n=== Condition Slug Lookup (app path) ===" -ForegroundColor Cyan
$conditionName = "Frightened"
$slug = $conditionName.ToLower().Replace(" ", "-")
try {
    $r = Invoke-RestMethod "$BASE/conditions/$slug" -Method GET -Headers $headers
    if ($r.name) { Pass "Condition '$conditionName' resolves via slug '$slug'" "index=$($r.index)" }
    else         { Fail "Condition slug '$slug'" "name missing in response" }
} catch { Fail "Condition slug '$slug'" $_.Exception.Message }

##############################################################
# RESULTS
##############################################################
Write-Host "`n=============================" -ForegroundColor Cyan
Write-Host "  PASSED : $passed" -ForegroundColor Green
if ($failed -gt 0) {
    Write-Host "  FAILED : $failed" -ForegroundColor Red
    Write-Host "`n  Some endpoints failed - check output above." -ForegroundColor Red
} else {
    Write-Host "  FAILED : $failed" -ForegroundColor Green
    Write-Host "`n  All endpoints verified. App API layer is ready." -ForegroundColor Green
}
Write-Host "=============================" -ForegroundColor Cyan

