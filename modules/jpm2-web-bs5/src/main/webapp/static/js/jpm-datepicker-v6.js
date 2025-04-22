function initTempusDominusField(input, dateFormat, locale) {
    if (!input || typeof tempusDominus === 'undefined')
        return;
    input.addEventListener('focus', function () {
        this.select();
    });

    function parseDate(str, pattern) {
        const regex = buildRegexFromPattern(pattern);
        const parts = str.match(regex);
        if (!parts)
            return null;

        const map = mapPatternToDateParts(pattern, parts);

        return new Date(
                map.yyyy ?? map.yy ?? new Date().getFullYear(),
                (map.MM ?? map.M ?? 1) - 1,
                map.dd ?? map.d ?? 1,
                map.HH ?? map.H ?? 0,
                map.mm ?? map.m ?? 0,
                map.ss ?? map.s ?? 0
                );
    }

    function formatDate(date, pattern) {
        const pad = (n, len = 2) => n.toString().padStart(len, '0');
        return pattern
                .replaceAll('yyyy', date.getFullYear())
                .replaceAll('yy', String(date.getFullYear()).slice(2))
                .replaceAll('MM', pad(date.getMonth() + 1))
                .replaceAll('M', date.getMonth() + 1)
                .replaceAll('dd', pad(date.getDate()))
                .replaceAll('d', date.getDate())
                .replaceAll('HH', pad(date.getHours()))
                .replaceAll('H', date.getHours())
                .replaceAll('mm', pad(date.getMinutes()))
                .replaceAll('m', date.getMinutes())
                .replaceAll('ss', pad(date.getSeconds()))
                .replaceAll('s', date.getSeconds());
    }

    function buildRegexFromPattern(pattern) {
        const tokenMap = {
            yyyy: '(\\d{4})',
            yy: '(\\d{2})',
            MM: '(\\d{2})',
            M: '(\\d{1,2})',
            dd: '(\\d{2})',
            d: '(\\d{1,2})',
            HH: '(\\d{2})',
            H: '(\\d{1,2})',
            mm: '(\\d{2})',
            m: '(\\d{1,2})',
            ss: '(\\d{2})',
            s: '(\\d{1,2})'
        };

        const tokensSorted = Object.keys(tokenMap).sort((a, b) => b.length - a.length);

        // Escapar los caracteres que no forman parte de los tokens
        let regexStr = '';
        let i = 0;
        while (i < pattern.length) {
            let matched = false;
            for (const token of tokensSorted) {
                if (pattern.slice(i, i + token.length) === token) {
                    regexStr += tokenMap[token];
                    i += token.length;
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                const c = pattern[i];
                regexStr += /[.*+?^${}()|[\]\\]/.test(c) ? '\\' + c : c;
                i++;
            }
        }

        return new RegExp(`^${regexStr}$`);
    }

    function mapPatternToDateParts(pattern, matches) {
        const tokenOrder = [];
        const tokenRegex = /(yyyy|yy|MM|M|dd|d|HH|H|mm|m|ss|s)/g;
        let m;
        while ((m = tokenRegex.exec(pattern)) !== null) {
            tokenOrder.push(m[1]);
        }

        const parts = {};
        tokenOrder.forEach((token, i) => {
            parts[token] = parseInt(matches[i + 1], 10);
        });

        return parts;
    }

    const rawValue = input.value;
    const defaultDate = rawValue ? parseDate(rawValue, dateFormat) : null;

    //console.log("Inicializando Tempus:", {rawValue, defaultDate});

    const picker = new tempusDominus.TempusDominus(input, {
        defaultDate: defaultDate instanceof Date && !isNaN(defaultDate) ? defaultDate : undefined,
        display: {
            components: {
                calendar: /d|M|y/.test(dateFormat),
                clock: /H|m|s/.test(dateFormat),
                hours: /H|h/.test(dateFormat),
                minutes: /m/.test(dateFormat),
                seconds: /s/.test(dateFormat)
            }
        },
        localization: {
            locale: locale,
            format: (date) => formatDate(date, dateFormat)
        }
    });


    input.addEventListener("change.td", () => {
        const selected = picker.dates.lastPicked;
        if (selected instanceof Date) {
            input.value = formatDate(selected, dateFormat);
        }
    });
    
    
}